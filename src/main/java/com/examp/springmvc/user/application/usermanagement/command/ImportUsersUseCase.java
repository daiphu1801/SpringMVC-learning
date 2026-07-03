package com.examp.springmvc.user.application.usermanagement.command;

import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.shared.domain.task.ExcelTask;
import com.examp.springmvc.shared.domain.task.ExcelTaskRepository;
import com.examp.springmvc.shared.domain.task.ExcelTaskStatus;
import com.examp.springmvc.shared.domain.task.ExcelTaskType;
import com.examp.springmvc.shared.infrastructure.task.ExcelThreadTracker;
import com.examp.springmvc.user.domain.model.Email;
import com.examp.springmvc.user.domain.model.Password;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.model.UserRole;
import com.examp.springmvc.user.domain.model.UserStatus;
import com.examp.springmvc.user.domain.ports.output.UserExcelParserPort;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class ImportUsersUseCase implements ImportUsersInputPort {

    private static final Logger LOG = LoggerFactory.getLogger(ImportUsersUseCase.class);
    private static final int BATCH_SIZE = 1000;

    private final ExcelTaskRepository excelTaskRepository;
    private final UserPersistencePort userPersistencePort;
    private final PasswordHasher passwordHasher;
    private final Executor excelDbWriterExecutor;
    private final PlatformTransactionManager transactionManager;
    private final UserExcelParserPort userExcelParserPort;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ImportUsersUseCase(
            ExcelTaskRepository excelTaskRepository,
            UserPersistencePort userPersistencePort,
            PasswordHasher passwordHasher,
            @Qualifier("excelDbWriterExecutor") Executor excelDbWriterExecutor,
            PlatformTransactionManager transactionManager,
            UserExcelParserPort userExcelParserPort) {
        this.excelTaskRepository = excelTaskRepository;
        this.userPersistencePort = userPersistencePort;
        this.passwordHasher = passwordHasher;
        this.excelDbWriterExecutor = excelDbWriterExecutor;
        this.transactionManager = transactionManager;
        this.userExcelParserPort = userExcelParserPort;
    }

    @Override
    @Async("excelExecutor")
    @SuppressFBWarnings("REC_CATCH_EXCEPTION")
    public void execute(ImportUsersCommand command) {
        String taskId = command.getTaskId();
        LOG.info("Starting excel import task: taskId={}", taskId);

        ExcelTask task = excelTaskRepository
                .findById(taskId)
                .orElse(new ExcelTask(
                        taskId,
                        ExcelTaskType.IMPORT,
                        ExcelTaskStatus.PROCESSING,
                        0,
                        0,
                        0,
                        0,
                        null,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now()));
        task.setStatus(ExcelTaskStatus.PROCESSING);
        excelTaskRepository.save(task);

        ConcurrentLinkedQueue<String> errors = new ConcurrentLinkedQueue<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        ExcelThreadTracker.setStatus("Đang đọc & phân tích file (Task ID: " + taskId + ")");
        try (InputStream is = command.getInputStream()) {
            List<UserImportRow> allRows = userExcelParserPort.parse(is);
            int totalRows = allRows.size();
            if (totalRows < 1) {
                throw new IllegalArgumentException("File Excel không có dữ liệu (trừ dòng tiêu đề)");
            }

            task.setTotalRows(totalRows);
            excelTaskRepository.save(task);

            List<UserImportRow> batchRows = new ArrayList<>();
            int batchNumber = 1;

            for (int i = 0; i < totalRows; i++) {
                UserImportRow importRow = allRows.get(i);
                batchRows.add(importRow);

                if (batchRows.size() >= BATCH_SIZE || i == totalRows - 1) {
                    List<UserImportRow> currentBatch = new ArrayList<>(batchRows);
                    batchRows.clear();

                    int currentBatchNum = batchNumber++;
                    CompletableFuture<Void> future = CompletableFuture.runAsync(
                            () -> {
                                processBatch(taskId, currentBatch, currentBatchNum, successCount, failedCount, errors);
                                updateTaskProgress(taskId, totalRows, successCount, failedCount, errors);
                            },
                            excelDbWriterExecutor);

                    futures.add(future);
                }
            }

            // Chờ cho tất cả luồng DB writers xử lý xong
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Cập nhật trạng thái cuối cùng
            ExcelTask finalTask = excelTaskRepository.findById(taskId).orElse(task);
            finalTask.setSuccessRows(successCount.get());
            finalTask.setFailedRows(failedCount.get());
            finalTask.setProgress(100);

            if (failedCount.get() > 0) {
                finalTask.setStatus(ExcelTaskStatus.COMPLETED_WITH_ERRORS);
            } else {
                finalTask.setStatus(ExcelTaskStatus.COMPLETED);
            }

            if (!errors.isEmpty()) {
                finalTask.setErrorSummary(String.join("\n", errors));
            }
            excelTaskRepository.save(finalTask);
            LOG.info(
                    "Import task completed: taskId={}, success={}, failed={}",
                    taskId,
                    successCount.get(),
                    failedCount.get());

        } catch (Exception e) {
            LOG.error("Import task failed unexpectedly: taskId={}", taskId, e);
            ExcelTask finalTask = excelTaskRepository.findById(taskId).orElse(task);
            finalTask.setStatus(ExcelTaskStatus.FAILED);
            errors.add("Lỗi hệ thống bất ngờ: " + e.getMessage());
            finalTask.setErrorSummary(String.join("\n", errors));
            excelTaskRepository.save(finalTask);
        } finally {
            ExcelThreadTracker.clearStatus();
        }
    }

    private void processBatch(
            String taskId,
            List<UserImportRow> batch,
            int batchNum,
            AtomicInteger successCount,
            AtomicInteger failedCount,
            ConcurrentLinkedQueue<String> errors) {

        // Sắp xếp theo username để tránh deadlock trong database
        batch.sort(Comparator.comparing(UserImportRow::getUsername));

        int startRow = batch.get(0).getRowNum();
        int endRow = batch.get(batch.size() - 1).getRowNum();
        int batchSuccess = 0;
        int batchFailed = 0;

        ExcelThreadTracker.setStatus(
                String.format("Ghi DB lô %d (dòng %d-%d) (Task: %s)", batchNum, startRow, endRow, taskId));
        try {
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            try {
                transactionTemplate.execute(status -> {
                    for (UserImportRow importRow : batch) {
                        if (importRow.hasErrors()) {
                            throw new RuntimeException("Trigger single-row fallback because row validation failed");
                        }
                        saveUser(importRow);
                    }
                    return null;
                });
                batchSuccess = batch.size();
                successCount.addAndGet(batchSuccess);

                LOG.info(
                        "Import batch completed: taskId={}, thread={}, batch={}, rows={}-{}, success={}, failed={}",
                        taskId,
                        Thread.currentThread().getName(),
                        batchNum,
                        startRow,
                        endRow,
                        batchSuccess,
                        batchFailed);

            } catch (Exception e) {
                // Khi có lỗi, fallback thực hiện lưu từng dòng để ghi nhận lỗi chi tiết
                LOG.warn(
                        "Batch failed. Falling back to single row processing for taskId={}, batch={}",
                        taskId,
                        batchNum);
                for (UserImportRow importRow : batch) {
                    if (importRow.hasErrors()) {
                        batchFailed++;
                        failedCount.incrementAndGet();
                        errors.add(String.format("Row %d: %s", importRow.getRowNum(), importRow.getErrorMessage()));
                        LOG.error(
                                "ERROR taskId={} thread={} row={} email={} reason={}",
                                taskId,
                                Thread.currentThread().getName(),
                                importRow.getRowNum(),
                                importRow.getEmail(),
                                importRow.getErrorMessage());
                        continue;
                    }

                    try {
                        transactionTemplate.execute(status -> {
                            saveUser(importRow);
                            return null;
                        });
                        batchSuccess++;
                        successCount.incrementAndGet();
                    } catch (Exception rowEx) {
                        batchFailed++;
                        failedCount.incrementAndGet();
                        String reason = getErrorMessage(rowEx);
                        errors.add(String.format("Row %d: %s", importRow.getRowNum(), reason));
                        LOG.error(
                                "ERROR taskId={} thread={} row={} email={} reason={}",
                                taskId,
                                Thread.currentThread().getName(),
                                importRow.getRowNum(),
                                importRow.getEmail(),
                                reason);
                    }
                }
                LOG.info(
                        "Import batch completed (with fallback): taskId={}, thread={}, batch={}, success={}, failed={}",
                        taskId,
                        Thread.currentThread().getName(),
                        batchNum,
                        batchSuccess,
                        batchFailed);
            }
        } finally {
            ExcelThreadTracker.clearStatus();
        }
    }

    private void saveUser(UserImportRow row) {
        Optional<User> existingUserOpt = userPersistencePort.findByUsername(row.getUsername());

        Email email = new Email(row.getEmail());
        UserRole role = UserRole.valueOf(row.getRole());
        UserStatus status = UserStatus.valueOf(row.getStatus());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            // Cập nhật thông tin profile
            existingUser.updateProfile(row.getFullName(), row.getPhone(), email);
            existingUser.changeRole(role);
            existingUser.changeStatus(status);

            // Cập nhật mật khẩu nếu trong file Excel có cung cấp
            if (row.getPassword() != null && !row.getPassword().trim().isEmpty()) {
                Password password = Password.fromRaw(row.getPassword(), passwordHasher);
                existingUser.changePassword(password);
            }

            existingUser.validate();
            userPersistencePort.save(existingUser);
        } else {
            Password password = Password.fromRaw(row.getPassword(), passwordHasher);
            User user = new User(
                    null,
                    row.getUsername(),
                    row.getFullName(),
                    email,
                    row.getPhone(),
                    status,
                    password,
                    role,
                    LocalDateTime.now(),
                    LocalDateTime.now());
            user.validate();
            userPersistencePort.save(user);
        }
    }

    private void updateTaskProgress(
            String taskId,
            int totalRows,
            AtomicInteger successCount,
            AtomicInteger failedCount,
            ConcurrentLinkedQueue<String> errors) {
        excelTaskRepository.findById(taskId).ifPresent(task -> {
            int processed = successCount.get() + failedCount.get();
            int progress = Math.min(99, (processed * 100) / totalRows);
            task.setProgress(progress);
            task.setSuccessRows(successCount.get());
            task.setFailedRows(failedCount.get());
            if (!errors.isEmpty()) {
                // Giới hạn độ dài summary để tránh CLOB quá tải
                List<String> list = new ArrayList<>(errors);
                if (list.size() > 500) {
                    list = list.subList(0, 500);
                    list.add("... (còn tiếp)");
                }
                task.setErrorSummary(String.join("\n", list));
            }
            excelTaskRepository.save(task);
        });
    }

    private String getErrorMessage(Exception e) {
        if (e.getMessage() != null && e.getMessage().contains("Username đã tồn tại")) {
            return "Username đã tồn tại";
        }
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            String msg = e.getCause().getMessage();
            if (msg.contains("UK_APP_USERS_EMAIL") || msg.contains("EMAIL")) {
                return "Email đã tồn tại";
            }
            if (msg.contains("UK_APP_USERS_USERNAME") || msg.contains("USERNAME")) {
                return "Username đã tồn tại";
            }
            return msg;
        }
        return e.getMessage() != null ? e.getMessage() : e.toString();
    }
}
