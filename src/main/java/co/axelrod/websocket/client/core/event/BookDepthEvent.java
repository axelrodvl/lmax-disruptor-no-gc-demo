package co.axelrod.websocket.client.core.event;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.core.model.BookDepth;

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