package ru.jetbrains.taskbalancer.standartimplementation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor {
    private ExecutorService executorService;
    private int maxQueueSize;

    public TaskExecutor(int maxThreads, int maxQueueSize) {
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        this.maxQueueSize = maxQueueSize;
    }

    public TaskExecutor(int maxQueueSize) {
        this(Runtime.getRuntime().availableProcessors() + 1, maxQueueSize);
    }


}
