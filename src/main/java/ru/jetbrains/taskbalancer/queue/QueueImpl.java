package ru.jetbrains.taskbalancer.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueImpl<T> extends ConcurrentLinkedQueue<T> {
    private int maxQueueSize;

    public QueueImpl(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public boolean putItem(T o) {
        if (this.size() < maxQueueSize) {
            super.add(o);
            return true;
        }
        return false;
    }

    public T pollItem() {
        return super.poll();
    }

    public T peekItem() {
        return super.peek();
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }
}
