package co.axelrod.websocket.client.event;

import java.nio.ByteBuffer;

public class PriceEvent {
    private ByteBuffer byteBuffer;

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }
}