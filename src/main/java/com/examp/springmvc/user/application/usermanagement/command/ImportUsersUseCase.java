package com.examp.springmvc.user.application.usermanagement.command;

import com.examp.springmvc.auth.domain.PasswordHasher;
import com.examp.springmvc.shared.domain.task.ExcelTask;
import com.examp.springmvc.shared.domain.task.ExcelTaskRepository;
import com.examp.springmvc.shared.domain.task.ExcelTaskStatus;
import com.examp.springmvc.shared.domain.task.ExcelTaskType;
import com.examp.springmvc.user.domain.model.Email;
import com.examp.springmvc.user.domain.model.Password;
import com.examp.springmvc.user.domain.model.User;
import com.examp.springmvc.user.domain.model.UserRole;
import com.examp.springmvc.user.domain.model.UserStatus;
import com.examp.springmvc.user.domain.ports.output.UserPersistencePort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ImportUsersUseCase(
            ExcelTaskRepository excelTaskRepository,
            UserPersistencePort userPersistencePort,
            PasswordHasher passwordHasher,
            @Qualifier("excelDbWriterExecutor") Executor excelDbWriterExecutor,
            PlatformTransactionManager transactionManager) {
        this.excelTaskRepository = excelTaskRepository;
        this.userPersistencePort = userPersistencePort;
        this.passwordHasher = passwordHasher;
        this.excelDbWriterExecutor = excelDbWriterExecutor;
        this.transactionManager = transactionManager;
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

        try (InputStream is = command.getInputStream();
                Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();
            if (totalRows < 1) {
                throw new IllegalArgumentException("File Excel không có dữ liệu (trừ dòng tiêu đề)");
            }

            task.setTotalRows(totalRows);
            excelTaskRepository.save(task);

            List<UserImportRow> batchRows = new ArrayList<>();
            int batchNumber = 1;

            for (int r = 1; r <= totalRows; r++) {
                Row row = sheet.getRow(r);
                if (row == null || isRowEmpty(row)) {
                    continue;
                }

                UserImportRow importRow = parseRow(row, r);
                batchRows.add(importRow);

                if (batchRows.size() >= BATCH_SIZE || r == totalRows) {
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
            LOG.warn("Batch failed. Falling back to single row processing for taskId={}, batch={}", taskId, batchNum);
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
    }

    private void saveUser(UserImportRow row) {
        // Kiểm tra trùng username sớm
        if (userPersistencePort.findByUsername(row.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        Email email = new Email(row.getEmail());
        Password password = Password.fromRaw(row.getPassword(), passwordHasher);
        UserRole role = UserRole.valueOf(row.getRole());
        UserStatus status = UserStatus.valueOf(row.getStatus());

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

    private UserImportRow parseRow(Row row, int rowNum) {
        String username = getCellValueAsString(row.getCell(0));
        String fullName = getCellValueAsString(row.getCell(1));
        String email = getCellValueAsString(row.getCell(2));
        String phone = getCellValueAsString(row.getCell(3));
        String password = getCellValueAsString(row.getCell(4));
        String role = getCellValueAsString(row.getCell(5));
        String status = getCellValueAsString(row.getCell(6));

        UserImportRow importRow = new UserImportRow(rowNum, username, fullName, email, phone, password, role, status);

        if (username.isEmpty()) {
            importRow.addError("Username không được để trống");
        }
        if (fullName.isEmpty()) {
            importRow.addError("Họ tên không được để trống");
        }
        if (email.isEmpty()) {
            importRow.addError("Email không được để trống");
        } else if (!email.contains("@")) {
            importRow.addError("Định dạng email không hợp lệ");
        }
        if (password.isEmpty()) {
            importRow.addError("Mật khẩu không được để trống");
        }

        if (role.isEmpty()) {
            importRow.setRole("USER");
        } else {
            try {
                UserRole.valueOf(role.toUpperCase());
                importRow.setRole(role.toUpperCase());
            } catch (Exception e) {
                importRow.addError("Vai trò không hợp lệ (Chỉ chấp nhận: USER, ADMIN)");
            }
        }

        if (status.isEmpty()) {
            importRow.setStatus("ACTIVE");
        } else {
            try {
                UserStatus.valueOf(status.toUpperCase());
                importRow.setStatus(status.toUpperCase());
            } catch (Exception e) {
                importRow.addError("Trạng thái không hợp lệ (Chỉ chấp nhận: ACTIVE, INACTIVE)");
            }
        }

        return importRow;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (long) numericValue) {
                    return String.valueOf((long) numericValue);
                }
                return String.valueOf(numericValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int c = 0; c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != org.apache.poi.ss.usermodel.CellType.BLANK) {
                return false;
            }
        }
        return true;
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

    private static class UserImportRow {
        private final int rowNum;
        private final String username;
        private final String fullName;
        private final String email;
        private final String phone;
        private final String password;
        private String role;
        private String status;
        private final List<String> rowErrors = new ArrayList<>();

        UserImportRow(
                int rowNum,
                String username,
                String fullName,
                String email,
                String phone,
                String password,
                String role,
                String status) {
            this.rowNum = rowNum;
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.password = password;
            this.role = role;
            this.status = status;
        }

        public int getRowNum() {
            return rowNum;
        }

        public String getUsername() {
            return username;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getPassword() {
            return password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void addError(String err) {
            rowErrors.add(err);
        }

        public boolean hasErrors() {
            return !rowErrors.isEmpty();
        }

        public String getErrorMessage() {
            return String.join(", ", rowErrors);
        }
    }
}
