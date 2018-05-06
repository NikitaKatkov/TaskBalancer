package ru.jetbrains.taskbalancer.threads;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleThread extends ThreadPoolExecutor {
    private final String threadName;

    public SimpleThread(String threadName, int maxQueueSize) {
        super(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(maxQueueSize));
        this.threadName = threadName;
    }

    public int getQueueCapacity() {
        return getQueue().remainingCapacity();
    }

    public CompletableFuture<Void> putTaskInQueue(Runnable task) {
        return CompletableFuture.runAsync(
                task,
                this
        );
    }

    public String getThreadName() {
        return threadName;
    }
}

