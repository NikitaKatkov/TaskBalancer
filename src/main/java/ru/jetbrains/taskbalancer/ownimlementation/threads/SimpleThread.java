package ru.jetbrains.taskbalancer.ownimlementation.threads;

import ru.jetbrains.taskbalancer.ownimlementation.queue.QueueImpl;

public class SimpleThread {
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
                    if (task != null)
                        task.run();
                    queue.pollItem();
                }
            }
        );
    }

    public int getQueueCapacity() {
        return queue.remainingCapacity();
    }

    public boolean putTaskInQueue(Runnable task) throws InterruptedException {
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

}

