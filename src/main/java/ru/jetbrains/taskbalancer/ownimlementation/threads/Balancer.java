package ru.jetbrains.taskbalancer.ownimlementation.threads;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Balancer {
    private static final Logger LOGGER = LogManager.getLogger(Balancer.class);
    private static final String THREAD_NAME_PATTERN = "thread_%d";

    private List<SimpleThread> executionThreads = new ArrayList<>();
    private SimpleThread maxCapacityThread;

    private Queue<Runnable> initialTasksQueue = new LinkedBlockingQueue<>();
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

        long time = System.currentTimeMillis();
        while (isRunning) {
            Runnable task = initialTasksQueue.peek();
            try {
                if (task == null) {
                    initialTasksQueue.poll();
                    continue;
                }

                if (maxCapacityThread.putTaskInQueue(task)) {
                    initialTasksQueue.poll();
                    LOGGER.info(String.format(
                            "Task send to thread %s queue", maxCapacityThread.getThreadName()));

                    findMaxCapacityQueueThread();

                    LOGGER.debug(String.format(
                            "Max queue space is in thread %s ", maxCapacityThread.getThreadName()));

                    if(initialTasksQueue.isEmpty())
                        LOGGER.debug(String.format(
                                "Last task was send to executor in %f seconds ", (System.currentTimeMillis() - time)/1000F));
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

}
