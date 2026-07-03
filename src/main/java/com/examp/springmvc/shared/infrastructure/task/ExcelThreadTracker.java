package com.examp.springmvc.shared.infrastructure.task;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ExcelThreadTracker {
    private static final Map<String, String> ACTIVE_THREADS = new ConcurrentHashMap<>();

    private ExcelThreadTracker() {}

    public static void setStatus(String status) {
        ACTIVE_THREADS.put(Thread.currentThread().getName(), status);
    }

    public static void clearStatus() {
        ACTIVE_THREADS.remove(Thread.currentThread().getName());
    }

    public static Map<String, String> getActiveThreads() {
        return Collections.unmodifiableMap(ACTIVE_THREADS);
    }
}
