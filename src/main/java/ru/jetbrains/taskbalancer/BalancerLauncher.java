package ru.jetbrains.taskbalancer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.jetbrains.taskbalancer.utils.Balancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BalancerLauncher {
    private static final Logger LOGGER_LAUNCHER = LogManager.getLogger(BalancerLauncher.class);

    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    private static final int MAX_QUEUE_SIZE_PER_THREAD = 3;
    private static final long WORKING_TIME_LIMIT = 15000L;

    public static void main(String[] args) {
        Balancer balancer = new Balancer(THREAD_POOL_SIZE, MAX_QUEUE_SIZE_PER_THREAD);
        generateTasks().forEach(balancer::addTaskToQueue);

        new Thread(() -> balancer.setRunning(true).startBalancing()).start();

        // принудительная остановка балансировщика "снаружи"
        try {
            Thread.sleep(WORKING_TIME_LIMIT);
        } catch (InterruptedException e) {
            LOGGER_LAUNCHER.error("Unable to invoke Thread.sleep() method", e);
        }
        balancer.setRunning(false);
        System.exit(0);
    }

    /**
     * @return список задач для балансировки
     */
    private static List<Runnable> generateTasks() {
        List<Runnable> tasks = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < 123; i++) {
            tasks.add(() -> {
                // поместите сюда код для ваших задач
                try {
                    Thread.sleep(random.nextInt(200));
                } catch (InterruptedException e) {
                    LOGGER_LAUNCHER.error("Error while trying to call Thread.sleep() method", e);
                }
            });
        }
        return tasks;
    }
}
