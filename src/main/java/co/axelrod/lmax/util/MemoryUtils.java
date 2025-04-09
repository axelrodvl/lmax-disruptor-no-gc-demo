package co.axelrod.lmax.util;

public class MemoryUtils {
    public static void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();     // Allocated memory
        long maxMemory = runtime.maxMemory();         // Max memory JVM can use
        long freeMemory = runtime.freeMemory();       // Free memory in allocated pool
        long usedMemory = totalMemory - freeMemory;   // Used memory in allocated pool

        System.out.println("---------------------------------------------------");
        System.out.println("Allocated memory (totalMemory): " + (totalMemory / 1024 / 1024) + " MB");
        System.out.println("Maximum memory (maxMemory): " + (maxMemory / 1024 / 1024) + " MB");
        System.out.println("Free memory: " + (freeMemory / 1024 / 1024) + " MB");
        System.out.println("Used memory: " + (usedMemory / 1024 / 1024) + " MB");
        System.out.println("---------------------------------------------------");
    }

    public static void allocateMemoryInCycle() throws InterruptedException {
        int size = 10_000_000;

        for (int i = 0; i < 1000; i++) {
            byte[] array = new byte[size];
            printMemoryUsage();
            Thread.sleep(1000L);
            System.out.println("Iteration: " + i + ", allocated " + (i + 1) * size / 1000000 + " MB");
        }

        System.out.println("Done");
    }
}
