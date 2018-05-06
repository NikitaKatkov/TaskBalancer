package ru.jetbrains.taskbalancer;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.jetbrains.taskbalancer.threads.SimpleThread;
import ru.jetbrains.taskbalancer.utils.Balancer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class BalancerTests {
    private Integer i1, i2, i3, i4, i5;
    private Integer[] integerArray = {i1, i2, i3, i4, i5};

    @BeforeClass
    public static void init() {

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
        thread.setRunning(true).start();
        // две задачи по 3 секунды точно не выполнятся сразу после запуска
        Assert.assertEquals(1, thread.getQueueCapacity());

        // и точно выполнятся после 7 секунд ожидания
        Thread.sleep(7000L);
        Assert.assertEquals(3, thread.getQueueCapacity());
    }

    @Test
    public void testRuntimeTasksSubmitting() throws InterruptedException {
        Balancer balancer = new Balancer(1, 1);
        new Thread(() -> balancer.setRunning(true).startBalancing()).start();

        for (int index = 0; index < 5; index++) {
            int finalIndex = index;
            balancer.addTaskToQueue(() -> {
                integerArray[finalIndex] = 1;
                System.out.println(String.format("element %d updated", finalIndex));
            });
        }


        Thread.sleep(10000L); // ожидание выполнения с запасом времени //todo заменить
        balancer.setRunning(false);
        Assert.assertTrue(Arrays.stream(integerArray).allMatch(Objects::nonNull));

    }
}
