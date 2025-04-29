package co.axelrod.websocket.client.disruptor.event;

import co.axelrod.websocket.client.config.Configuration;

import java.nio.ByteBuffer;

public class BookDepthEvent {
    private final BookDepth bookDepth = new BookDepth(Configuration.MARKET_DEPTH);
    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(Configuration.WS_MAX_CONTENT_LENGTH);

    public BookDepth getBookDepth() {
        return bookDepth;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }
}