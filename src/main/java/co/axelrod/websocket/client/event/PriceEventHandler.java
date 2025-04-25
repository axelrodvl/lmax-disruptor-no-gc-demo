package co.axelrod.websocket.client.event;

import co.axelrod.websocket.client.logging.ConsoleWriter;
import com.lmax.disruptor.EventHandler;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

public class PriceEventHandler implements EventHandler<PriceEvent> {
    public static final byte[] BYTES = ") ".getBytes();

    private final AtomicInteger eventCount = new AtomicInteger(0);

    @Override
    public void onEvent(PriceEvent event, long sequence, boolean endOfBatch) {
        eventCount.incrementAndGet();
        ByteBuffer byteBuffer = event.getByteBuffer();
        ConsoleWriter.write(eventCount.get());
        ConsoleWriter.write(BYTES);
        ConsoleWriter.writeWithNewLine(byteBuffer);
    }

    public int getEventCount() {
        return eventCount.get();
    }
}