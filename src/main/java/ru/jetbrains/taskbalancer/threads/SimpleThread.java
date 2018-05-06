package ru.jetbrains.taskbalancer.threads;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.jetbrains.taskbalancer.queue.QueueImpl;

import java.util.concurrent.Executor;

public class SimpleThread implements Executor {
    private static final Logger LOGGER_THREAD = LogManager.getLogger(SimpleThread.class);
    private final QueueImpl queue;
    private final String threadName;
    private final Thread thread;


    private volatile boolean isRunning = false;

    public SimpleThread(String threadName, int maxQueueSize) {
        this.threadName = threadName;
        this.queue = new QueueImpl<Runnable>(maxQueueSize);
        this.thread = new Thread(
            () -> {
                while (isRunning) {
                    Runnable task = (Runnable) queue.peekItem();
                    if (task != null) {
                        try {
                            task.run();
                        } catch (Exception e) {
                            LOGGER_THREAD.error(String.format("Error during call() method execution in thread %s", threadName));
                        }
                        LOGGER_THREAD.debug(String.format(
                                "Thread %s finished execution of another task. Task polled out of thread queue", threadName));
                    }
                    queue.pollItem();
                }
            }
        );
    }

    public int getQueueCapacity() {
        return queue.getMaxQueueSize() - queue.size();
    }

    public boolean putTaskInQueue(Runnable task) {
        return queue.putItem(task);
    }

    public String getThreadName() {
        return threadName;
    }

    public void start() {
        thread.start();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public SimpleThread setRunning(boolean running) {
        isRunning = running;
        return this;
    }

    @Override
    public void execute(Runnable runnable) {
        queue.add(runnable);
    }
}

