package co.axelrod.websocket.client.disruptor.event;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.logging.ConsoleWriter;
import com.lmax.disruptor.EventHandler;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import static co.axelrod.websocket.client.parser.BinanceBookDepthParser.parseBookDepth;

public class BookDepthEventHandler implements EventHandler<BookDepthEvent> {
    public static final byte[] BYTES = ") ".getBytes();

    private final AtomicInteger eventCount = new AtomicInteger(0);

    @Override
    public void onEvent(BookDepthEvent event, long sequence, boolean endOfBatch) {
        eventCount.incrementAndGet();

        ByteBuffer byteBuffer = event.getByteBuffer();
        ConsoleWriter.write(eventCount.get());
        ConsoleWriter.write(BYTES);
        ConsoleWriter.writeWithNewLine(byteBuffer);

        parseBookDepth(byteBuffer, event.getBookDepth());

        for (int level = 0; level < Configuration.MARKET_DEPTH; level++) {
            ConsoleWriter.write(event.getBookDepth().getBids().get(level).getPriceLevel());
        }
    }

    public int getEventCount() {
        return eventCount.get();
    }
}