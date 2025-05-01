package co.axelrod.websocket.client.integration.provider.binance;

import co.axelrod.websocket.client.core.disruptor.DisruptorFactory;
import co.axelrod.websocket.client.core.event.BookDepthEvent;
import co.axelrod.websocket.client.core.event.BookDepthEventHandler;
import co.axelrod.websocket.client.integration.netty.WebSocketClient;
import co.axelrod.websocket.client.subscription.InstrumentsManager;
import com.lmax.disruptor.dsl.Disruptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BinanceWebSocketClientTest {
    @Test
    public void testAddInstrument() throws Exception {
        BookDepthEventHandler bookDepthEventHandler = new BookDepthEventHandler();
        Disruptor<BookDepthEvent> disruptor = DisruptorFactory.getDisruptor(bookDepthEventHandler);

        InstrumentsManager instrumentsManager = new InstrumentsManager();

        BinanceWebSocketClient webSocketClient = new BinanceWebSocketClient(disruptor, instrumentsManager);

        disruptor.start();
        webSocketClient.start();

        Thread.sleep(10000);

        instrumentsManager.subscribe("bnbbtc");

        Thread.sleep(10000);

        webSocketClient.stop();
        disruptor.shutdown();
    }

    @Test
    public void testStartStopClient() throws Exception {
        BookDepthEventHandler bookDepthEventHandler = new BookDepthEventHandler();
        Disruptor<BookDepthEvent> disruptor = DisruptorFactory.getDisruptor(bookDepthEventHandler);

        InstrumentsManager instrumentsManager = new InstrumentsManager();

        WebSocketClient webSocketClient = new BinanceWebSocketClient(disruptor, instrumentsManager);

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
