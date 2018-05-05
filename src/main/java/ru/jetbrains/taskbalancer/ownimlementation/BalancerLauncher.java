package ru.jetbrains.taskbalancer.ownimlementation;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.jetbrains.taskbalancer.ownimlementation.threads.Balancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BalancerLauncher {
    private static final Logger LOGGER_LAUNCHER = LogManager.getLogger(BalancerLauncher.class);

    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    private static final int MAX_QUEUE_SIZE_PER_THREAD = 1;

    public static void main(String[] args) throws InterruptedException {
        Balancer balancer = new Balancer(THREAD_POOL_SIZE, MAX_QUEUE_SIZE_PER_THREAD);

        generateTasks().forEach(balancer::addTaskToQueue);

        long time  = System.currentTimeMillis();
        new Thread(() -> balancer.setRunning(true).startBalancing()).start();

        // остановка балансировщика "снаружи"
        Thread.sleep(30000L);
        balancer.setRunning(false);
    }

    private static List<Runnable> generateTasks() {
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < 123; i++) {
            tasks.add(() -> {
                double result = new Random().nextDouble();
                for (int k = 0; k < 1000000; k++) {
                    result += Math.tan(result);
                }
//                try {
//                    Thread.sleep(1000L);
//                } catch (InterruptedException e) {
//                    LOGGER_LAUNCHER.error("Error while trying to call Thread.sleep() method", e);
//                }
            });
        }
        return tasks;
    }
}
