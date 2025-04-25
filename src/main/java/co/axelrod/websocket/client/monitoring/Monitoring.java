package co.axelrod.websocket.client.monitoring;

import co.axelrod.websocket.client.logging.ConsoleWriter;
import co.axelrod.websocket.client.event.PriceEvent;
import co.axelrod.websocket.client.event.PriceEventHandler;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.TimeUnit;

public class Monitoring {
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
            printDisruptorState();
            printHandledEvents();
            MemoryUtils.printMemoryUsage();
            TimeUnit.SECONDS.sleep(DELAY_IN_SECONDS);
        }
    }

    private void printDisruptorState() {
        ConsoleWriter.write("---------------------------------------------------");
        ConsoleWriter.write("Disruptor buffer size: ", disruptor.getRingBuffer().getBufferSize());
    }

    private void printHandledEvents() {
        ConsoleWriter.write("---------------------------------------------------");
        ConsoleWriter.write("Handled events: ", priceEventHandler.getEventCount());
    }
}
