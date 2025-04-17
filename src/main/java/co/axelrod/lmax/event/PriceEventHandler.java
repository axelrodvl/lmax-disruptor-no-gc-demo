package co.axelrod.lmax.event;

import co.axelrod.lmax.util.ConsoleWriter;
import com.lmax.disruptor.EventHandler;
import io.netty.buffer.ByteBuf;

public class PriceEventHandler implements EventHandler<PriceEvent> {
    @Override
    public void onEvent(PriceEvent event, long sequence, boolean endOfBatch) {
        ByteBuf buf = event.getBuffer();
        ConsoleWriter.write(buf);
    }
}