package com.examp.springmvc.shared.domain.task;

public final class ThreadPoolMetrics {
    private final int activeCount;
    private final int queueSize;
    private final int poolSize;
    private final int maxPoolSize;
    private final long completedTaskCount;

    public ThreadPoolMetrics(int activeCount, int queueSize, int poolSize, int maxPoolSize, long completedTaskCount) {
        this.activeCount = activeCount;
        this.queueSize = queueSize;
        this.poolSize = poolSize;
        this.maxPoolSize = maxPoolSize;
        this.completedTaskCount = completedTaskCount;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public long getCompletedTaskCount() {
        return completedTaskCount;
    }
}
