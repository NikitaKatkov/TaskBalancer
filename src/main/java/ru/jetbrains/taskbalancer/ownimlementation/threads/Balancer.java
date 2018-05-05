package ru.jetbrains.taskbalancer.ownimlementation.threads;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.jetbrains.taskbalancer.ownimlementation.models.ThreadInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Balancer {
    private static final Logger LOGGER = LogManager.getLogger(Balancer.class);
    private static final String THREAD_NAME_PATTERN = "thread_%d";

    private List<ThreadInfo> executionThreads = new ArrayList<>();
    private ThreadInfo maxCapacityThreadInfo;

    private Queue<Runnable> initialTasksQueue = new LinkedBlockingQueue<>();
    private boolean isRunning = false;

    public Balancer(int threadPoolSize, int maxQueueSizePerThread) {
        for (int threadNumber = 0; threadNumber < threadPoolSize; threadNumber++) {
            String threadName = String.format(THREAD_NAME_PATTERN, threadNumber);
            executionThreads.add(
                    new ThreadInfo(new SingleThread(threadName, maxQueueSizePerThread), maxQueueSizePerThread));
        }
        maxCapacityThreadInfo = executionThreads.get(0);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void startBalancing() {
        executionThreads.forEach(threadInfo -> threadInfo.getThread().setRunning(true).start());

        while (isRunning) {

            Object task = initialTasksQueue.peek();
            try {
                if (task != null && maxCapacityThreadInfo.getThread().putTaskInQueue(task)) {
                    initialTasksQueue.poll();
                    updateCurrentThreadInfo(maxCapacityThreadInfo);
                    LOGGER.info(String.format(
                            "Task send to thread %s queue",
                            maxCapacityThreadInfo.getThread().getThreadName()));
                }
            } catch (InterruptedException e) {
                LOGGER.error("Unable to put task into thread queue ", e);
            }
        }
        LOGGER.warn("Stop balancer execution");
    }

    public void addTaskToQueue(Runnable task) {
        initialTasksQueue.add(task);
    }

    private int getThreadQueueCapacity(SingleThread thread) {
        return thread.getQueueCapacity();
    }

    private void updateCurrentThreadInfo(ThreadInfo currentThreadInfo) {
        currentThreadInfo.setThreadQueueCapacity(currentThreadInfo.getThreadQueueCapacity());
        int currentMaxCapacity = maxCapacityThreadInfo.getThreadQueueCapacity();

        for (ThreadInfo threadInfo: executionThreads) {
                int potentialMaxCapacity = threadInfo.getThreadQueueCapacity();
                if (potentialMaxCapacity > currentMaxCapacity) {
                    currentMaxCapacity = potentialMaxCapacity;
                    maxCapacityThreadInfo = threadInfo;
                }
        }
    }

}
