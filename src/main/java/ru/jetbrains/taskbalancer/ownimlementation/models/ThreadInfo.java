package ru.jetbrains.taskbalancer.ownimlementation.models;

import ru.jetbrains.taskbalancer.ownimlementation.threads.SingleThread;

public class ThreadInfo {
    private SingleThread thread;
    private int threadQueueCapacity;

    public ThreadInfo(SingleThread thread, int threadQueueCapacity) {
        this.thread = thread;
        this.threadQueueCapacity = threadQueueCapacity;
    }

    public SingleThread getThread() {
        return thread;
    }

    public int getThreadQueueCapacity() {
        return threadQueueCapacity;
    }

    public void setThreadQueueCapacity(int threadQueueCapacity) {
        this.threadQueueCapacity = threadQueueCapacity;
    }
}
