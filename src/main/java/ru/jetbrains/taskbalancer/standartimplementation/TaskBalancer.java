package ru.jetbrains.taskbalancer.standartimplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class TaskBalancer {
    // версия из интернета просто чтобы было с чем сравнить - не соответствует тз, но для тестов сойдет
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() + 1);

        long time = System.currentTimeMillis();

        List<Future<Double>> futures = new ArrayList<>();
        for (int i = 0; i < 123; i++) {
            final int j = i;
            futures.add(
                    CompletableFuture.supplyAsync(
                            () -> {
                                double result = new Random().nextDouble();
                                for (int k = 0; k < 1000000; k++) {
                                    result += Math.tan(result);
                                }
                                return result;
                            },
                            threadPool
                    ));
        }

        double value = 0;
        for (Future<Double> future : futures) {
            value += future.get();
        }
        System.out.println(System.currentTimeMillis() - time);

        threadPool.shutdown();
    }
}
