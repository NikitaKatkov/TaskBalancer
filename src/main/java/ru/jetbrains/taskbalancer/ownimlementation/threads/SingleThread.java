package ru.jetbrains.taskbalancer.ownimlementation.threads;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.jetbrains.taskbalancer.ownimlementation.queue.QueueImpl;

public class SingleThread {
    private Logger LOGGER_THREAD = LogManager.getLogger(SingleThread.class);
    private final QueueImpl queue;
    private final String threadName;
    private final Thread thread;

    private volatile boolean isRunning = false;

    public SingleThread(String threadName, int maxQueueSize) {
        this.threadName = threadName;
        this.queue = new QueueImpl<Runnable>(maxQueueSize);
        this.thread = new Thread(
                () -> {
                    while (isRunning) {
                        Runnable task = (Runnable) queue.pollItem();
                        if (task != null)
                            task.run();
                    }
                }
        );
    }

    public int getQueueCapacity() {
        return queue.remainingCapacity();
    }

    public boolean putTaskInQueue(Object task) throws InterruptedException {
        return queue.putItem(task);
    }

    public String getThreadName() {
        return threadName;
    }

    public SingleThread start() {
        thread.start();
        return this;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public SingleThread setRunning(boolean running) {
        isRunning = running;
        return this;
    }

}

