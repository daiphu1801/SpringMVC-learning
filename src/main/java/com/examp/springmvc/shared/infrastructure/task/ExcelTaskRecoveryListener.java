package com.examp.springmvc.shared.infrastructure.task;

import com.examp.springmvc.shared.domain.task.ExcelTask;
import com.examp.springmvc.shared.domain.task.ExcelTaskRepository;
import com.examp.springmvc.shared.domain.task.ExcelTaskStatus;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ExcelTaskRecoveryListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelTaskRecoveryListener.class);
    private final ExcelTaskRepository excelTaskRepository;

    public ExcelTaskRecoveryListener(ExcelTaskRepository excelTaskRepository) {
        this.excelTaskRepository = excelTaskRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Tránh chạy trùng lắp ở Child Context
        if (event.getApplicationContext().getParent() == null) {
            LOG.info("Server started/refreshed. Scanning for orphaned Excel tasks...");
            List<ExcelTask> recentTasks = excelTaskRepository.findRecentTasks(50);
            for (ExcelTask task : recentTasks) {
                if (task.getStatus() == ExcelTaskStatus.PROCESSING) {
                    LOG.warn(
                            "Found orphaned task: taskId={}, type={}. Marking as FAILED.",
                            task.getId(),
                            task.getType());
                    task.setStatus(ExcelTaskStatus.FAILED);
                    task.setErrorSummary("Tiến trình bị gián đoạn do sự cố máy chủ tắt đột ngột");
                    excelTaskRepository.save(task);
                }
            }
        }
    }
}
