package ru.jetbrains.taskbalancer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.jetbrains.taskbalancer.utils.Balancer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BalancerLauncher {
    private static final Logger LOGGER_LAUNCHER = LogManager.getLogger(BalancerLauncher.class);

    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    private static final int MAX_QUEUE_SIZE_PER_THREAD = 1;
    private static final long WORKING_TIME_LIMIT = 30000L;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Balancer balancer = new Balancer(THREAD_POOL_SIZE, MAX_QUEUE_SIZE_PER_THREAD);

        generateTasks().forEach(balancer::addTaskToQueue);

        new Thread(() -> balancer.setRunning(true).startBalancing()).start();

        // ожидание завершения всех задач
//        for (Future future: balancer.getExecutionResults())
//            future.get();

        // принудительная остановка балансировщика "снаружи"
//        try {
//            Thread.sleep(WORKING_TIME_LIMIT);
//        } catch (InterruptedException e) {
//            LOGGER_LAUNCHER.error("Unable to invoke Thread.sleep() method", e);
//        }
        balancer.setRunning(false);
    }

    private static List<Runnable> generateTasks() {
        List<Runnable> tasks = new ArrayList<>();

        for (int i = 0; i < 123; i++) {
            tasks.add(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    LOGGER_LAUNCHER.error("Error while trying to call Thread.sleep() method", e);
                }
            });
        }
        return tasks;
    }
}
