package co.axelrod.websocket.client.event;

import co.axelrod.websocket.client.console.ConsoleWriter;
import com.lmax.disruptor.EventHandler;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.atomic.AtomicInteger;

public class PriceEventHandler implements EventHandler<PriceEvent> {
    private final AtomicInteger eventCount = new AtomicInteger(0);

    @Override
    public void onEvent(PriceEvent event, long sequence, boolean endOfBatch) {
        eventCount.incrementAndGet();
        ByteBuf buffer = event.getBuffer();
        ConsoleWriter.write(buffer);
    }

    public int getEventCount() {
        return eventCount.get();
    }
}