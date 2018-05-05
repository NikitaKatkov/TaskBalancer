package ru.jetbrains.taskbalancer.ownimlementation;

import ru.jetbrains.taskbalancer.ownimlementation.threads.Balancer;

public class MainClass {
    public static void main(String[] args) {
        Balancer balancer = new Balancer(2, 3);

        for (int i = 0; i < 3; i++) {
            balancer.addTaskToQueue(() -> {
                try {
                    Thread.sleep(30000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        balancer.setRunning(true);
        balancer.startBalancing();
    }
}
