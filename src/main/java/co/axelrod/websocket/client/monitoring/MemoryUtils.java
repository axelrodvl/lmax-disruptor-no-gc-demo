package co.axelrod.websocket.client.monitoring;

import co.axelrod.websocket.client.logging.ConsoleWriter;

public class MemoryUtils {
    public static void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();

        // Allocated memory
        long totalMemory = runtime.totalMemory();

        // Max memory JVM can use
        long maxMemory = runtime.maxMemory();

        // Free memory in allocated pool
        long freeMemory = runtime.freeMemory();

        // Used memory in allocated pool
        long usedMemory = totalMemory - freeMemory;

        ConsoleWriter.write("---------------------------------------------------");
        ConsoleWriter.write("Allocated memory (totalMemory): ", totalMemory / 1024, " KB");
        ConsoleWriter.write("Maximum memory (maxMemory): ", (maxMemory / 1024), " KB");
        ConsoleWriter.write("Free memory: ", (freeMemory / 1024), " KB");
        ConsoleWriter.write("Used memory: ", (usedMemory / 1024), " KB");
        ConsoleWriter.write("---------------------------------------------------");
    }
}
