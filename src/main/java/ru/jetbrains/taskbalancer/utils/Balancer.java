package ru.jetbrains.taskbalancer.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.jetbrains.taskbalancer.threads.SimpleThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

public class Balancer {
    private static final Logger LOGGER = LogManager.getLogger(Balancer.class);
    private static final String THREAD_NAME_PATTERN = "thread_%d";

    private List<SimpleThread> executionThreads = new ArrayList<>();
    private SimpleThread maxCapacityThread;

    private List<Future> executionResults = new ArrayList<>(); //fixme Collections.syncList ??

    private ConcurrentLinkedQueue<Runnable> initialTasksQueue = new ConcurrentLinkedQueue<>();
    private boolean isRunning = false;

    public Balancer(int threadPoolSize, int maxQueueSizePerThread) {
        for (int threadNumber = 0; threadNumber < threadPoolSize; threadNumber++) {
            String threadName = String.format(THREAD_NAME_PATTERN, threadNumber);
            executionThreads.add(new SimpleThread(threadName, maxQueueSizePerThread));
        }
        maxCapacityThread = executionThreads.get(0);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Balancer setRunning(boolean running) {
        isRunning = running;
        return this;
    }

    public void startBalancing() {
        executionThreads.forEach(threadInfo -> threadInfo.setRunning(true).start());

        while (isRunning) {
            Runnable task = initialTasksQueue.peek();
            if (task == null) {
                initialTasksQueue.poll();
                continue;
            }

            if (maxCapacityThread.getQueueCapacity() > 0) {
                executionResults.add(
                        CompletableFuture.runAsync(
                                initialTasksQueue.poll(),
                                maxCapacityThread
                        )
                );

//                maxCapacityThread.execute(task);
//                initialTasksQueue.poll();
                LOGGER.info(String.format(
                        "Task send to thread %s queue", maxCapacityThread.getThreadName()));
            }

            findMaxCapacityQueueThread();
            LOGGER.debug(String.format(
                    "Max queue space is in thread %s ", maxCapacityThread.getThreadName()));
        }
        LOGGER.warn("Stop balancer execution");
    }

    public void addTaskToQueue(Runnable task) {
        initialTasksQueue.add(task);
    }

    private void findMaxCapacityQueueThread() {
        int currentMaxCapacity = maxCapacityThread.getQueueCapacity();
        for (SimpleThread thread: executionThreads) {
                int potentialMaxCapacity = thread.getQueueCapacity();
                if (potentialMaxCapacity > currentMaxCapacity) {
                    currentMaxCapacity = potentialMaxCapacity;
                    maxCapacityThread = thread;
                }
        }
    }

    public List<Future> getExecutionResults() {
        return executionResults;
    }
}