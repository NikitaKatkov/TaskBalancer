package ru.jetbrains.taskbalancer.ownimlementation.queue;

import java.util.concurrent.LinkedBlockingQueue;

public class QueueImpl<T> extends LinkedBlockingQueue<T> {

    public QueueImpl(int maxQueueSize) {
        super(maxQueueSize);
    }

    public boolean putItem(T o) throws InterruptedException {
        if (this.remainingCapacity() > 0) {
            super.put(o);
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
}
