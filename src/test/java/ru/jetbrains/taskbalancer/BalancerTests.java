package ru.jetbrains.taskbalancer;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.jetbrains.taskbalancer.threads.SimpleThread;
import ru.jetbrains.taskbalancer.utils.Balancer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BalancerTests {

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
                File file = new File(finalIndex + ".txt");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        Thread.sleep(3000L);
//        for (Future future: balancer.getExecutionResults()) {
//            try {
//                future.get();
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }

        balancer.setRunning(false);

    }

//    @AfterClass
    public static void destroy() {
        for (int i = 0; i < 5; i++) {
            File file = new File(i + ".txt");
            if (file.exists())
                file.delete();
        }
    }
}
