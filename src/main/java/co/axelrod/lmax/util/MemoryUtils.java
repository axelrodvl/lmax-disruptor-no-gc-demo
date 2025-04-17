package co.axelrod.lmax.util;

public class MemoryUtils {
    public static void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();     // Allocated memory
        long maxMemory = runtime.maxMemory();         // Max memory JVM can use
        long freeMemory = runtime.freeMemory();       // Free memory in allocated pool
        long usedMemory = totalMemory - freeMemory;   // Used memory in allocated pool

        System.out.println("---------------------------------------------------");
        System.out.println("Allocated memory (totalMemory): " + (totalMemory / 1024) + " KB");
        System.out.println("Maximum memory (maxMemory): " + (maxMemory / 1024) + " KB");
        System.out.println("Free memory: " + (freeMemory / 1024) + " KB");
        System.out.println("Used memory: " + (usedMemory / 1024) + " KB");
        System.out.println("---------------------------------------------------");
    }
}
