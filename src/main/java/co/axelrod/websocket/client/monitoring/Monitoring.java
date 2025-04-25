package co.axelrod.websocket.client.monitoring;

import co.axelrod.websocket.client.logging.ConsoleWriter;
import co.axelrod.websocket.client.event.PriceEvent;
import co.axelrod.websocket.client.event.PriceEventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import io.netty.buffer.*;

import java.util.concurrent.TimeUnit;

import static co.axelrod.websocket.client.logging.ConsoleConstant.DELIMITER;
import static co.axelrod.websocket.client.logging.ConsoleConstant.KB;

public class Monitoring {
    public static final byte[] DISRUPTOR_BUFFER_SIZE = "Disruptor buffer size: ".getBytes();
    public static final byte[] USED_HEAP_MEMORY = "Used heap memory: ".getBytes();
    public static final byte[] USED_DIRECT_MEMORY = "Used direct memory: ".getBytes();
    public static final byte[] HANDLED_EVENTS = "Handled events: ".getBytes();

    private final PriceEventHandler priceEventHandler;
    private final Disruptor<PriceEvent> disruptor;

    private static final int DELAY_IN_SECONDS = 10;

    public Monitoring(PriceEventHandler priceEventHandler, Disruptor<PriceEvent> disruptor) {
        this.priceEventHandler = priceEventHandler;
        this.disruptor = disruptor;
    }

    public void start() {
        Thread monitoringThread = new Thread(() -> {
            try {
                printState();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        monitoringThread.setName("Monitoring Thread");
        monitoringThread.setDaemon(true);
        monitoringThread.start();
    }

    private void printState() throws InterruptedException {
        while (true) {
            printHandledEvents();
            printDisruptorState();
            printNettyState();
            MemoryUtils.printMemoryUsage();
            TimeUnit.SECONDS.sleep(DELAY_IN_SECONDS);
        }
    }

    private void printHandledEvents() {
        ConsoleWriter.writeWithNewLine(DELIMITER);

        ConsoleWriter.write(HANDLED_EVENTS);
        ConsoleWriter.writeWithNewLine(priceEventHandler.getEventCount());
    }

    private void printDisruptorState() {
        ConsoleWriter.writeWithNewLine(DELIMITER);

        ConsoleWriter.write(DISRUPTOR_BUFFER_SIZE);
        ConsoleWriter.writeWithNewLine(disruptor.getRingBuffer().getBufferSize());
    }

    private void printNettyState() {
        PooledByteBufAllocatorMetric metric = PooledByteBufAllocator.DEFAULT.metric();
        ConsoleWriter.writeWithNewLine(DELIMITER);

        ConsoleWriter.write(USED_HEAP_MEMORY);
        ConsoleWriter.write((int) metric.usedHeapMemory() / 1024);
        ConsoleWriter.writeWithNewLine(KB);

        ConsoleWriter.write(USED_DIRECT_MEMORY);
        ConsoleWriter.write((int) metric.usedDirectMemory() / 1024);
        ConsoleWriter.writeWithNewLine(KB);
    }
}
