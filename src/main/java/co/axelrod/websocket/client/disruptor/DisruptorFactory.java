package co.axelrod.websocket.client.disruptor;

import co.axelrod.websocket.client.event.PriceEvent;
import co.axelrod.websocket.client.event.PriceEventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

public class DisruptorFactory {
    private static final int DISRUPTOR_BUFFER_SIZE = 16;

    public static Disruptor<PriceEvent> getDisruptor() {
        Disruptor<PriceEvent> disruptor = new Disruptor<>(
                PriceEvent::new,
                DISRUPTOR_BUFFER_SIZE,
                DaemonThreadFactory.INSTANCE);

        PriceEventHandler priceEventHandler = new PriceEventHandler();
        disruptor.handleEventsWith(priceEventHandler);

        return disruptor;
    }
}
