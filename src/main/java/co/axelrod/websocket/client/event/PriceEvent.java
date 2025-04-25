package co.axelrod.websocket.client.event;

import co.axelrod.websocket.client.config.Configuration;

import java.nio.ByteBuffer;

public class PriceEvent {
    private final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(Configuration.WS_MAX_CONTENT_LENGTH);

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }
}