package com.examp.springmvc.user.application.usermanagement.query;

import com.examp.springmvc.shared.domain.task.ExcelTask;
import com.examp.springmvc.shared.domain.task.ExcelTaskRepository;
import com.examp.springmvc.shared.domain.task.ExcelTaskStatus;
import com.examp.springmvc.shared.domain.task.ExcelTaskType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ExportUsersUseCase implements ExportUsersInputPort {

    private static final Logger LOG = LoggerFactory.getLogger(ExportUsersUseCase.class);

    private final ExcelTaskRepository excelTaskRepository;
    private final UserQueryPort userQueryPort;

    public ExportUsersUseCase(ExcelTaskRepository excelTaskRepository, UserQueryPort userQueryPort) {
        this.excelTaskRepository = excelTaskRepository;
        this.userQueryPort = userQueryPort;
    }

    @Override
    @Async("excelExecutor")
    @SuppressFBWarnings("REC_CATCH_EXCEPTION")
    public void execute(ExportUsersCommand command) {
        String taskId = command.getTaskId();
        LOG.info("Starting excel export task: taskId={}", taskId);

        ExcelTask task = excelTaskRepository
                .findById(taskId)
                .orElse(new ExcelTask(
                        taskId,
                        ExcelTaskType.EXPORT,
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

        // Đảm bảo thư mục lưu trữ tạm thời tồn tại trong workspace
        File exportDir = new File("target/exports");
        if (!exportDir.exists() && !exportDir.mkdirs()) {
            LOG.error("Failed to create export directory: {}", exportDir.getAbsolutePath());
            task.setStatus(ExcelTaskStatus.FAILED);
            task.setErrorSummary("Không thể tạo thư mục lưu trữ file export");
            excelTaskRepository.save(task);
            return;
        }

        File file = new File(exportDir, "users_export_" + taskId + ".xlsx");

        // Dùng SXSSFWorkbook với memory window 100 để tránh tràn bộ nhớ (OOM)
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("Users");

            // Viết Header
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Username", "Họ Tên", "Email", "Số Điện Thoại", "Trạng Thái", "Vai Trò"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            List<UserDTO> users = userQueryPort.findAll();
            int totalRows = users.size();
            task.setTotalRows(totalRows);
            excelTaskRepository.save(task);

            LOG.info("Writing {} users to Excel file", totalRows);

            for (int i = 0; i < totalRows; i++) {
                UserDTO user = users.get(i);
                Row row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(user.getUsername());
                row.createCell(1).setCellValue(user.getFullName());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.getPhone() != null ? user.getPhone() : "");
                row.createCell(4).setCellValue(user.getStatus());
                row.createCell(5).setCellValue(user.getRole());

                // Cập nhật tiến độ sau mỗi 200 dòng
                if ((i + 1) % 200 == 0 || (i + 1) == totalRows) {
                    int progress = Math.min(99, ((i + 1) * 100) / totalRows);
                    task.setProgress(progress);
                    task.setSuccessRows(i + 1);
                    excelTaskRepository.save(task);

                    // Thêm chút sleep giả lập xử lý ngầm mượt mà
                    Thread.sleep(10);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            workbook.dispose();

            task.setProgress(100);
            task.setSuccessRows(totalRows);
            task.setStatus(ExcelTaskStatus.COMPLETED);
            task.setResultUrl("/users/excel/download/" + taskId);
            excelTaskRepository.save(task);

            LOG.info("Export task completed successfully: taskId={}", taskId);

        } catch (Exception e) {
            LOG.error("Export task failed: taskId={}", taskId, e);
            task.setStatus(ExcelTaskStatus.FAILED);
            task.setErrorSummary("Lỗi xuất file: " + e.getMessage());
            excelTaskRepository.save(task);
        }
    }
}
