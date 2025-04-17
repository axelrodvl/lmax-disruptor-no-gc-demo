package co.axelrod.lmax.event;

import io.netty.buffer.ByteBuf;

public class PriceEvent {
    private ByteBuf buffer;

    public void setBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public ByteBuf getBuffer() {
        return buffer;
    }
}