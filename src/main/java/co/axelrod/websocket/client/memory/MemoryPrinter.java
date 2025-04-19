package co.axelrod.websocket.client.memory;

import java.util.concurrent.TimeUnit;

public class MemoryPrinter {
    private static final int DELAY_IN_SECONDS = 10;

    public void printMemoryUsageInLoop() {
        Thread printerThread = new Thread(() -> {
            try {
                printMemoryUsage();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        printerThread.setDaemon(true);
        printerThread.start();
    }

    private void printMemoryUsage() throws InterruptedException {
        while (true) {
            MemoryUtils.printMemoryUsage();
            TimeUnit.SECONDS.sleep(DELAY_IN_SECONDS);
        }
    }
}
