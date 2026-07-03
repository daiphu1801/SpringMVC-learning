package com.examp.springmvc.shared.application.task;

import com.examp.springmvc.shared.domain.task.ExcelDashboardDTO;
import com.examp.springmvc.shared.domain.task.ExcelTask;
import com.examp.springmvc.shared.domain.task.ExcelTaskRepository;
import com.examp.springmvc.shared.domain.task.ExcelThreadPoolMetricsPort;
import com.examp.springmvc.shared.infrastructure.task.ExcelThreadTracker;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GetExcelDashboardUseCase implements GetExcelDashboardInputPort {

    private final ExcelThreadPoolMetricsPort excelThreadPoolMetricsPort;
    private final ExcelTaskRepository excelTaskRepository;

    public GetExcelDashboardUseCase(
            ExcelThreadPoolMetricsPort excelThreadPoolMetricsPort, ExcelTaskRepository excelTaskRepository) {
        this.excelThreadPoolMetricsPort = excelThreadPoolMetricsPort;
        this.excelTaskRepository = excelTaskRepository;
    }

    @Override
    public ExcelDashboardDTO execute() {
        List<ExcelTask> recentTasks = excelTaskRepository.findRecentTasks(5);
        return new ExcelDashboardDTO(
                excelThreadPoolMetricsPort.getReaderPoolMetrics(),
                excelThreadPoolMetricsPort.getWriterPoolMetrics(),
                ExcelThreadTracker.getActiveThreads(),
                recentTasks);
    }
}
