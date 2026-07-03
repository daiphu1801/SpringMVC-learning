package com.examp.springmvc.shared.infrastructure.task;

import com.examp.springmvc.shared.domain.task.ExcelThreadPoolMetricsPort;
import com.examp.springmvc.shared.domain.task.ThreadPoolMetrics;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class SpringExcelThreadPoolMetricsAdapter implements ExcelThreadPoolMetricsPort {

    private final ThreadPoolTaskExecutor excelExecutor;
    private final ThreadPoolTaskExecutor excelDbWriterExecutor;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public SpringExcelThreadPoolMetricsAdapter(
            @Qualifier("excelExecutor") ThreadPoolTaskExecutor excelExecutor,
            @Qualifier("excelDbWriterExecutor") ThreadPoolTaskExecutor excelDbWriterExecutor) {
        this.excelExecutor = excelExecutor;
        this.excelDbWriterExecutor = excelDbWriterExecutor;
    }

    @Override
    public ThreadPoolMetrics getReaderPoolMetrics() {
        return new ThreadPoolMetrics(
                excelExecutor.getActiveCount(),
                excelExecutor.getThreadPoolExecutor().getQueue().size(),
                excelExecutor.getThreadPoolExecutor().getPoolSize(),
                excelExecutor.getMaxPoolSize(),
                excelExecutor.getThreadPoolExecutor().getCompletedTaskCount());
    }

    @Override
    public ThreadPoolMetrics getWriterPoolMetrics() {
        return new ThreadPoolMetrics(
                excelDbWriterExecutor.getActiveCount(),
                excelDbWriterExecutor.getThreadPoolExecutor().getQueue().size(),
                excelDbWriterExecutor.getThreadPoolExecutor().getPoolSize(),
                excelDbWriterExecutor.getMaxPoolSize(),
                excelDbWriterExecutor.getThreadPoolExecutor().getCompletedTaskCount());
    }
}
