package com.examp.springmvc.shared.domain.task;

public interface ExcelThreadPoolMetricsPort {
    ThreadPoolMetrics getReaderPoolMetrics();

    ThreadPoolMetrics getWriterPoolMetrics();
}
