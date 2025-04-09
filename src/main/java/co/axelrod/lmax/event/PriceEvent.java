package co.axelrod.lmax.event;

import java.nio.ByteBuffer;

public class PriceEvent {
    private ByteBuffer value;

    public void setValue(ByteBuffer value) {
        this.value = value;
    }

    public ByteBuffer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PriceEvent{" + "value=" + value + '}';
    }
}