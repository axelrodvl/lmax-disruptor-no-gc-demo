package co.axelrod.websocket.client.netty;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.core.disruptor.DisruptorFactory;
import co.axelrod.websocket.client.core.event.BookDepthEvent;
import co.axelrod.websocket.client.core.event.BookDepthEventHandler;
import co.axelrod.websocket.client.integration.netty.WebSocketClient;
import co.axelrod.websocket.client.integration.provider.binance.BinanceWebSocketClient;
import co.axelrod.websocket.client.subscription.InstrumentsManager;
import com.lmax.disruptor.dsl.Disruptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebSocketClientTest {
    @Test
    public void testStartStopClient() throws Exception {
        BookDepthEventHandler bookDepthEventHandler = new BookDepthEventHandler();
        Disruptor<BookDepthEvent> disruptor = DisruptorFactory.getDisruptor(bookDepthEventHandler);

        InstrumentsManager instrumentsManager = new InstrumentsManager();

        WebSocketClient webSocketClient = new BinanceWebSocketClient(disruptor, Configuration.BINANCE_WS_URI, instrumentsManager);

        disruptor.start();

        assertEquals(0, bookDepthEventHandler.getEventCount());

        webSocketClient.start();
        Thread.sleep(10000);
        assertTrue(bookDepthEventHandler.getEventCount() > 0);

        webSocketClient.stop();
        Thread.sleep(10000);

        webSocketClient.start();
        Thread.sleep(10000);

        webSocketClient.stop();
        disruptor.shutdown();
    }
}
