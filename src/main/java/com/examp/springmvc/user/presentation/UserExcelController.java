package com.examp.springmvc.user.presentation;

import com.examp.springmvc.shared.application.task.GetExcelDashboardInputPort;
import com.examp.springmvc.shared.domain.task.ExcelDashboardDTO;
import com.examp.springmvc.shared.domain.task.ExcelTask;
import com.examp.springmvc.shared.domain.task.ExcelTaskRepository;
import com.examp.springmvc.shared.domain.task.ThreadPoolMetrics;
import com.examp.springmvc.user.application.usermanagement.command.ImportUsersCommand;
import com.examp.springmvc.user.application.usermanagement.command.ImportUsersInputPort;
import com.examp.springmvc.user.application.usermanagement.query.ExportUsersCommand;
import com.examp.springmvc.user.application.usermanagement.query.ExportUsersInputPort;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/users/excel")
public class UserExcelController {

    private static final Logger LOG = LoggerFactory.getLogger(UserExcelController.class);

    private final ImportUsersInputPort importUsersInputPort;
    private final ExportUsersInputPort exportUsersInputPort;
    private final GetExcelDashboardInputPort getExcelDashboardInputPort;
    private final ExcelTaskRepository excelTaskRepository;

    public UserExcelController(
            ImportUsersInputPort importUsersInputPort,
            ExportUsersInputPort exportUsersInputPort,
            GetExcelDashboardInputPort getExcelDashboardInputPort,
            ExcelTaskRepository excelTaskRepository) {
        this.importUsersInputPort = importUsersInputPort;
        this.exportUsersInputPort = exportUsersInputPort;
        this.getExcelDashboardInputPort = getExcelDashboardInputPort;
        this.excelTaskRepository = excelTaskRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "user/excel_dashboard";
    }

    @PostMapping("/import")
    public void importExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        if (file.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(response, "{\"error\":\"File is empty\"}");
            return;
        }

        String taskId = UUID.randomUUID().toString();
        LOG.info("Initiating import task: taskId={}", taskId);

        try {
            importUsersInputPort.execute(new ImportUsersCommand(taskId, file.getInputStream()));
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            writeJson(response, String.format("{\"taskId\":\"%s\"}", taskId));
        } catch (Exception e) {
            LOG.error("Failed to trigger import task: taskId={}", taskId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, String.format("{\"error\":\"%s\"}", e.getMessage()));
        }
    }

    @PostMapping("/export")
    public void exportExcel(HttpServletResponse response) throws IOException {
        String taskId = UUID.randomUUID().toString();
        LOG.info("Initiating export task: taskId={}", taskId);

        try {
            exportUsersInputPort.execute(new ExportUsersCommand(taskId));
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            writeJson(response, String.format("{\"taskId\":\"%s\"}", taskId));
        } catch (Exception e) {
            LOG.error("Failed to trigger export task: taskId={}", taskId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(response, String.format("{\"error\":\"%s\"}", e.getMessage()));
        }
    }

    @GetMapping("/metrics")
    public void getThreadPoolMetrics(HttpServletResponse response) throws IOException {
        ExcelDashboardDTO dashboard = getExcelDashboardInputPort.execute();

        ThreadPoolMetrics reader = dashboard.getReaderPool();
        ThreadPoolMetrics writer = dashboard.getWriterPool();

        StringBuilder threadsJson = new StringBuilder("[");
        int index = 0;
        for (Map.Entry<String, String> entry : dashboard.getActiveThreads().entrySet()) {
            threadsJson.append(String.format(
                    "{\"threadName\":\"%s\",\"action\":\"%s\"}",
                    entry.getKey(), entry.getValue().replace("\"", "\\\"")));
            if (++index < dashboard.getActiveThreads().size()) {
                threadsJson.append(",");
            }
        }
        threadsJson.append("]");

        String json = String.format(
                "{\"readerPool\":{\"activeCount\":%d,\"queueSize\":%d,\"poolSize\":%d,"
                        + "\"maxPoolSize\":%d,\"completedTaskCount\":%d},"
                        + "\"writerPool\":{\"activeCount\":%d,\"queueSize\":%d,\"poolSize\":%d,"
                        + "\"maxPoolSize\":%d,\"completedTaskCount\":%d},"
                        + "\"activeThreads\":%s}",
                reader.getActiveCount(),
                reader.getQueueSize(),
                reader.getPoolSize(),
                reader.getMaxPoolSize(),
                reader.getCompletedTaskCount(),
                writer.getActiveCount(),
                writer.getQueueSize(),
                writer.getPoolSize(),
                writer.getMaxPoolSize(),
                writer.getCompletedTaskCount(),
                threadsJson.toString());
        writeJson(response, json);
    }

    @GetMapping("/tasks")
    public void getRecentTasks(HttpServletResponse response) throws IOException {
        ExcelDashboardDTO dashboard = getExcelDashboardInputPort.execute();
        writeJson(response, toJson(dashboard.getRecentTasks()));
    }

    @GetMapping("/tasks/{id}")
    public void getTaskStatus(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
        ExcelTask task = excelTaskRepository.findById(id).orElse(null);
        if (task == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeJson(response, "{\"error\":\"Task not found\"}");
            return;
        }
        writeJson(response, toJson(task));
    }

    @GetMapping("/tasks/{id}/errors")
    public void getTaskErrors(@PathVariable("id") String id, HttpServletResponse response) throws IOException {
        ExcelTask task = excelTaskRepository.findById(id).orElse(null);
        if (task == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Task không tồn tại");
            return;
        }
        response.setContentType("text/plain;charset=UTF-8");
        String summary = task.getErrorSummary();
        response.getWriter().write(summary != null ? summary : "Không có lỗi nào.");
    }

    @GetMapping("/download/{taskId}")
    public void downloadExportedFile(@PathVariable("taskId") String taskId, HttpServletResponse response)
            throws IOException {
        File file = new File("target/exports/users_export_" + taskId + ".xlsx");
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("File xuất không tồn tại hoặc đã bị xóa.");
            return;
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"users_export_" + taskId + ".xlsx\"");
        response.setContentLengthLong(file.length());

        try (FileInputStream fis = new FileInputStream(file);
                OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    private void writeJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(json);
    }

    private String toJson(ExcelTask task) {
        return String.format(
                "{\"id\":\"%s\",\"type\":\"%s\",\"status\":\"%s\",\"progress\":%d,"
                        + "\"totalRows\":%d,\"successRows\":%d,\"failedRows\":%d,\"resultUrl\":%s}",
                task.getId(),
                task.getType().name(),
                task.getStatus().name(),
                task.getProgress(),
                task.getTotalRows() != null ? task.getTotalRows() : 0,
                task.getSuccessRows() != null ? task.getSuccessRows() : 0,
                task.getFailedRows() != null ? task.getFailedRows() : 0,
                task.getResultUrl() != null ? "\"" + task.getResultUrl() + "\"" : "null");
    }

    private String toJson(List<ExcelTask> tasks) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(toJson(tasks.get(i)));
            if (i < tasks.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
