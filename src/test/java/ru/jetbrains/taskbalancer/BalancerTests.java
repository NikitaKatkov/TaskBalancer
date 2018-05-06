package ru.jetbrains.taskbalancer;

import org.junit.Assert;
import org.junit.Test;
import ru.jetbrains.taskbalancer.threads.SimpleThread;
import ru.jetbrains.taskbalancer.utils.Balancer;

public class BalancerTests {
    private static int counter = 0;
    private synchronized void incrementCounter() {
        counter++;
        try {
            Thread.sleep(100); // имитация ресурсоемкой задачи
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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
        // первая задача в очереди начинает выполняться, вторая ожидает
        Assert.assertEquals(2, thread.getQueueCapacity());

        // все задачи точно выполнятся после 7 секунд ожидания
        Thread.sleep(7000L);
        Assert.assertEquals(3, thread.getQueueCapacity());
    }

    @Test
    public void testTasksSubmitting() throws InterruptedException {
        Balancer balancer = new Balancer(2, 3);
        for (int i = 0; i < 3; i++) {
            balancer.addTaskToQueue(this::incrementCounter);
        }
        new Thread(() -> balancer.setRunning(true).startBalancing()).start();
        Thread.sleep(400); // время с запасом
        Assert.assertEquals(3, counter);

        balancer.addTaskToQueue(this::incrementCounter);
        balancer.setRunning(false);
    }

    @Test
    public void testTaskOverhead() throws InterruptedException {
        Balancer balancer = new Balancer(1, 2);
        for (int index = 0; index < 4; index++) {
            int finalIndex = index;
            balancer.addTaskToQueue(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(finalIndex);
            });
        }

        Assert.assertEquals(4, balancer.getQueueSize());
        new Thread(() -> balancer.setRunning(true).startBalancing()).start();
        // максимальный размер очереди 2, работает 1 поток, значит через 500 мс после старта одна задача из очереди потока
        // все еще будет обрабатываться, 2 задачи  будут ожидать в очереди потока и 1 задача - в очереди балансировщика
        Thread.sleep(500L);
        Assert.assertEquals(1, balancer.getQueueSize());
    }
}
