package co.axelrod.lmax.event;

import com.lmax.disruptor.EventFactory;

public class PriceEventFactory implements EventFactory<PriceEvent> {
    @Override
    public PriceEvent newInstance() {
        return new PriceEvent();
    }
}