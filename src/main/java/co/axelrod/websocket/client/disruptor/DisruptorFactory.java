package co.axelrod.websocket.client.disruptor;

import co.axelrod.websocket.client.disruptor.event.BookDepthEvent;
import co.axelrod.websocket.client.disruptor.event.BookDepthEventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

public class DisruptorFactory {
    private static final int DISRUPTOR_BUFFER_SIZE = 16;

    public static Disruptor<BookDepthEvent> getDisruptor(BookDepthEventHandler bookDepthEventHandler) {
        Disruptor<BookDepthEvent> disruptor = new Disruptor<>(
                BookDepthEvent::new,
                DISRUPTOR_BUFFER_SIZE,
                DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith(bookDepthEventHandler);

        return disruptor;
    }
}
