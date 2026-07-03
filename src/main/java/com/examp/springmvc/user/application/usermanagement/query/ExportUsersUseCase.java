package com.examp.springmvc.user.application.usermanagement.query;

import com.examp.springmvc.shared.domain.task.ExcelTask;
import com.examp.springmvc.shared.domain.task.ExcelTaskRepository;
import com.examp.springmvc.shared.domain.task.ExcelTaskStatus;
import com.examp.springmvc.shared.domain.task.ExcelTaskType;
import com.examp.springmvc.shared.infrastructure.task.ExcelThreadTracker;
import com.examp.springmvc.user.domain.ports.output.UserExcelExporterPort;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ExportUsersUseCase implements ExportUsersInputPort {

    private static final Logger LOG = LoggerFactory.getLogger(ExportUsersUseCase.class);

    private final ExcelTaskRepository excelTaskRepository;
    private final UserQueryPort userQueryPort;
    private final UserExcelExporterPort userExcelExporterPort;

    public ExportUsersUseCase(
            ExcelTaskRepository excelTaskRepository,
            UserQueryPort userQueryPort,
            UserExcelExporterPort userExcelExporterPort) {
        this.excelTaskRepository = excelTaskRepository;
        this.userQueryPort = userQueryPort;
        this.userExcelExporterPort = userExcelExporterPort;
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

        ExcelThreadTracker.setStatus("Xuất file: Đang truy vấn dữ liệu (Task ID: " + taskId + ")");
        try {
            List<UserDTO> users = userQueryPort.findAll();
            int totalRows = users.size();
            task.setTotalRows(totalRows);
            excelTaskRepository.save(task);

            LOG.info("Writing {} users to Excel file", totalRows);
            ExcelThreadTracker.setStatus("Xuất file: Đang tạo tệp Excel (Task ID: " + taskId + ")");

            try (FileOutputStream fos = new FileOutputStream(file)) {
                userExcelExporterPort.export(users, fos, successRows -> {
                    int progress = Math.min(99, (successRows * 100) / totalRows);
                    task.setProgress(progress);
                    task.setSuccessRows(successRows);
                    excelTaskRepository.save(task);
                });
            }

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
        } finally {
            ExcelThreadTracker.clearStatus();
        }
    }
}
