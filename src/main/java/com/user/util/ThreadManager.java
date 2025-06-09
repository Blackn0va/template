package com.user.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.user.discord.DiscordBot;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class ThreadManager {
    private static final Logger logger = LoggerFactory.getLogger(ThreadManager.class);
    private static final ThreadManager INSTANCE = new ThreadManager();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Map<String, List<Runnable>> taskGroups = new HashMap<>();
    private final Map<String, ThreadPoolExecutor> executors = new HashMap<>();
    private final AtomicInteger threadIndex = new AtomicInteger(0);
    private DiscordBot discordBot;

    private ThreadManager() {}

    public static ThreadManager getInstance() {
        return INSTANCE;
    }

    public synchronized void registerTaskGroup(String groupName, int poolSize) {
        if (taskGroups.containsKey(groupName)) {
            throw new IllegalArgumentException("Task-Gruppe existiert bereits: " + groupName);
        }
        taskGroups.put(groupName, new ArrayList<>());
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
        executors.put(groupName, executor);
        logger.info("🔧 Neue Task-Gruppe registriert: {} mit Pool-Größe: {}", groupName, poolSize);
    }

    public synchronized void unregisterTaskGroup(String groupName) {
        List<Runnable> tasks = taskGroups.remove(groupName);
        if (tasks != null) {
            ThreadPoolExecutor executor = executors.remove(groupName);
            executor.shutdown();
            logger.info("🗑️ Task-Gruppe entfernt: {}", groupName);
        }
    }

    private synchronized void taskCompleted(String groupName, Runnable task) {
        List<Runnable> tasks = taskGroups.get(groupName);
        if (tasks != null) {
            tasks.remove(task);
            logger.info("✅ Task in Gruppe {} abgeschlossen. Verbleibende Tasks: {}", groupName, tasks.size());
        }
    }

    public synchronized void submitTask(String groupName, Runnable task) {
        ThreadPoolExecutor executor = executors.get(groupName);
        if (executor == null) {
            throw new IllegalArgumentException("Task-Gruppe nicht gefunden: " + groupName);
        }
        taskGroups.get(groupName).add(task);
        executor.submit(() -> {
            try {
                task.run();
            } finally {
                taskCompleted(groupName, task);
            }
        });
    }

    public synchronized List<Runnable> getPendingTasks(String groupName) {
        return taskGroups.getOrDefault(groupName, new ArrayList<>());
    }

    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return scheduler.schedule(command, delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public synchronized void initializeDiscordBot(String token) {
        if (discordBot == null) {
            discordBot = new DiscordBot(token);
            logger.info("🤖 Discord-Bot wurde initialisiert");
        }
    }

    public synchronized DiscordBot getDiscordBot() {
        return discordBot;
    }

    public synchronized void shutdown() {
        if (discordBot != null) {
            discordBot.shutdown();
        }
        scheduler.shutdown();
        for (String groupName : executors.keySet()) {
            unregisterTaskGroup(groupName);
        }
        logger.info("ThreadManager wurde vollständig heruntergefahren.");
    }
}
