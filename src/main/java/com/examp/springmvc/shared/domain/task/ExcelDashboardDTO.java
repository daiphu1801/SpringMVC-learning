package com.examp.springmvc.shared.domain.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ExcelDashboardDTO {
    private final ThreadPoolMetrics readerPool;
    private final ThreadPoolMetrics writerPool;
    private final Map<String, String> activeThreads;
    private final List<ExcelTask> recentTasks;

    public ExcelDashboardDTO(
            ThreadPoolMetrics readerPool,
            ThreadPoolMetrics writerPool,
            Map<String, String> activeThreads,
            List<ExcelTask> recentTasks) {
        this.readerPool = readerPool;
        this.writerPool = writerPool;
        this.activeThreads = activeThreads != null ? new HashMap<>(activeThreads) : null;
        this.recentTasks = recentTasks != null ? new ArrayList<>(recentTasks) : null;
    }

    public ThreadPoolMetrics getReaderPool() {
        return readerPool;
    }

    public ThreadPoolMetrics getWriterPool() {
        return writerPool;
    }

    public Map<String, String> getActiveThreads() {
        return activeThreads != null ? Collections.unmodifiableMap(activeThreads) : null;
    }

    public List<ExcelTask> getRecentTasks() {
        return recentTasks != null ? Collections.unmodifiableList(recentTasks) : null;
    }
}
