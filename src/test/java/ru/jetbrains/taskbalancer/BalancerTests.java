package ru.jetbrains.taskbalancer;

import org.junit.Assert;
import org.junit.Test;
import ru.jetbrains.taskbalancer.ownimlementation.threads.SimpleThread;

public class BalancerTests {

    @Test
    public void testSingleThreadQueueWork() throws InterruptedException {
        SimpleThread thread = new SimpleThread("customThread", 3);
        for (int i = 0; i < 2; i++) {
            thread.putTaskInQueue(() -> {
                try {
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        thread.setRunning(true).start();
        // две задачи по 3 секунды точно не выполнятся сразу после запуска
        Assert.assertEquals(1, thread.getQueueCapacity());

        // и точно выполнятся после 7 секунд ожидания
        Thread.sleep(7000L);
        Assert.assertEquals(3, thread.getQueueCapacity());
    }

    @Test
    public void test() {

    }
}
