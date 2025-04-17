package co.axelrod.websocket.client.event;

import co.axelrod.websocket.client.util.ConsoleWriter;
import com.lmax.disruptor.EventHandler;
import io.netty.buffer.ByteBuf;

public class PriceEventHandler implements EventHandler<PriceEvent> {
    @Override
    public void onEvent(PriceEvent event, long sequence, boolean endOfBatch) {
        ByteBuf buffer = event.getBuffer();
        ConsoleWriter.write(buffer);
    }
}