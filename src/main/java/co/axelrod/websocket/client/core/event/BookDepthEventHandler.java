package co.axelrod.websocket.client.core.event;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.util.logging.ConsoleWriter;
import com.lmax.disruptor.EventHandler;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import static co.axelrod.websocket.client.util.logging.ConsoleConstant.SLASH;
import static co.axelrod.websocket.client.util.logging.ConsoleConstant.SPACE;
import static co.axelrod.websocket.client.integration.provider.binance.BinanceBookDepthParser.parseBookDepth;

public class BookDepthEventHandler implements EventHandler<BookDepthEvent> {
    public static final byte[] BYTES = ") ".getBytes();

    private final AtomicInteger eventCount = new AtomicInteger(0);

    @Override
    public void onEvent(BookDepthEvent event, long sequence, boolean endOfBatch) {
        eventCount.incrementAndGet();

        ByteBuffer byteBuffer = event.getByteBuffer();
        printRawEvent(byteBuffer);

        parseBookDepth(byteBuffer, event.getBookDepth());

        printParsedEvent(event);
    }

    private void printRawEvent(ByteBuffer byteBuffer) {
        ConsoleWriter.write(eventCount.get());
        ConsoleWriter.write(BYTES);
        ConsoleWriter.writeWithNewLine(byteBuffer);
        byteBuffer.flip();
    }

    private static void printParsedEvent(BookDepthEvent event) {
        ConsoleWriter.writeWithNewLine(SPACE);
        ConsoleWriter.writeWithNewLine(event.getBookDepth().getStream());
        for (int level = 0; level < Configuration.MARKET_DEPTH; level++) {
            ConsoleWriter.write(event.getBookDepth().getBids().get(level).getPriceLevel());
            ConsoleWriter.write(SLASH);
            ConsoleWriter.write(event.getBookDepth().getAsks().get(level).getPriceLevel());
            ConsoleWriter.write(SPACE);
        }
        ConsoleWriter.writeWithNewLine(SPACE);
    }

    public int getEventCount() {
        return eventCount.get();
    }
}