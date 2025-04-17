package co.axelrod.lmax.event;

import com.lmax.disruptor.EventHandler;
import io.netty.buffer.ByteBuf;

public class PriceEventHandler implements EventHandler<PriceEvent> {
    @Override
    public void onEvent(PriceEvent event, long sequence, boolean endOfBatch) {
        ByteBuf buf = event.getBuffer();
        int len = buf.readableBytes();
        try {
            int readerIndex = buf.readerIndex();
            for (int i = 0; i < len; i++) {
                System.out.write(buf.getByte(readerIndex + i));
            }
            System.out.write('\n');
        } finally {
            buf.release();
        }
    }
}