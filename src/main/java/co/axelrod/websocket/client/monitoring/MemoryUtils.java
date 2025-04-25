package co.axelrod.websocket.client.monitoring;

import co.axelrod.websocket.client.logging.ConsoleWriter;

import static co.axelrod.websocket.client.logging.ConsoleConstant.*;

public class MemoryUtils {
    public static final byte[] ALLOCATED_MEMORY = "Allocated memory (totalMemory): ".getBytes();
    public static final byte[] MAXIMUM_MEMORY = "Maximum memory (maxMemory): ".getBytes();
    public static final byte[] FREE_MEMORY = "Free memory: ".getBytes();
    public static final byte[] USER_MEMORY = "Used memory: ".getBytes();

    private static long freeMemoryPreviousValue = 0;
    private static long usedMemoryPreviousValue = 0;

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

        ConsoleWriter.writeWithNewLine(DELIMITER);

        ConsoleWriter.write(ALLOCATED_MEMORY);
        ConsoleWriter.write((int) totalMemory / 1024);
        ConsoleWriter.writeWithNewLine(KB);

        ConsoleWriter.write(MAXIMUM_MEMORY);
        ConsoleWriter.write((int) (maxMemory / 1024));
        ConsoleWriter.writeWithNewLine(KB);

        ConsoleWriter.write(FREE_MEMORY);
        ConsoleWriter.write((int) (freeMemory / 1024));
        ConsoleWriter.write(KB);
        ConsoleWriter.write(SPACE);
        ConsoleWriter.write(OPEN_BRACKET);
        ConsoleWriter.write((int) (freeMemory - freeMemoryPreviousValue) / 1024);
        ConsoleWriter.write(KB);
        ConsoleWriter.writeWithNewLine(CLOSE_BRACKET);

        ConsoleWriter.write(USER_MEMORY);
        ConsoleWriter.write((int) (usedMemory / 1024));
        ConsoleWriter.write(KB);
        ConsoleWriter.write(SPACE);
        ConsoleWriter.write(OPEN_BRACKET);
        ConsoleWriter.write((int) (usedMemory - usedMemoryPreviousValue) / 1024);
        ConsoleWriter.write(KB);
        ConsoleWriter.writeWithNewLine(CLOSE_BRACKET);

        ConsoleWriter.writeWithNewLine(DELIMITER);

        freeMemoryPreviousValue = freeMemory;
        usedMemoryPreviousValue = usedMemory;
    }
}
