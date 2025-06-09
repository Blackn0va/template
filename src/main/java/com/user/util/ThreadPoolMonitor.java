package com.user.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ThreadPoolMonitor {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolMonitor.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ThreadPoolExecutor threadPoolExecutor;
    private final long monitorInterval;
    private final TimeUnit timeUnit;

    public ThreadPoolMonitor(ThreadPoolExecutor threadPoolExecutor, long monitorInterval, TimeUnit timeUnit) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.monitorInterval = monitorInterval;
        this.timeUnit = timeUnit;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::logThreadPoolStats, 0, monitorInterval, timeUnit);
    }

    public void stop() {
        scheduler.shutdown();
    }

    private void logThreadPoolStats() {
        int activeCount = threadPoolExecutor.getActiveCount();
        int poolSize = threadPoolExecutor.getPoolSize();
        int corePoolSize = threadPoolExecutor.getCorePoolSize();
        int maxPoolSize = threadPoolExecutor.getMaximumPoolSize();
        long taskCount = threadPoolExecutor.getTaskCount();
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        long pendingTasks = taskCount - completedTaskCount;

        logger.info(
                "📊 ThreadPool Status - Aktiv: {}, Pool-Größe: {}, Kern-Pool-Größe: {}, Max-Pool-Größe: {}, Task-Anzahl: {}, Abgeschlossene Tasks: {}, Ausstehende Tasks: {}",
                activeCount, poolSize, corePoolSize, maxPoolSize, taskCount, completedTaskCount, pendingTasks);

        logThreadStates();
    }

    private void logThreadStates() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);

        Map<Thread.State, Long> threadStateCounts = new HashMap<>();
        for (ThreadInfo threadInfo : threadInfos) {
            threadStateCounts.merge(threadInfo.getThreadState(), 1L, Long::sum);
        }

        String threadStateLog = threadStateCounts.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        logger.info("🔍 Thread-Zustände: {}", threadStateLog);
    }
}
