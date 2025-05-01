package co.axelrod.websocket.client;

import co.axelrod.websocket.client.core.disruptor.DisruptorFactory;
import co.axelrod.websocket.client.core.event.BookDepthEvent;
import co.axelrod.websocket.client.core.event.BookDepthEventHandler;
import co.axelrod.websocket.client.util.monitoring.Monitoring;
import co.axelrod.websocket.client.integration.netty.WebSocketClient;
import co.axelrod.websocket.client.integration.provider.binance.BinanceWebSocketClient;
import co.axelrod.websocket.client.subscription.InstrumentsManager;
import com.lmax.disruptor.dsl.Disruptor;

public class Application {
    public static void main(String[] args) throws Exception {
        BookDepthEventHandler bookDepthEventHandler = new BookDepthEventHandler();
        Disruptor<BookDepthEvent> disruptor = DisruptorFactory.getDisruptor(bookDepthEventHandler);

        InstrumentsManager instrumentsManager = new InstrumentsManager();

        try (WebSocketClient webSocketClient = new BinanceWebSocketClient(disruptor, instrumentsManager);
             Monitoring monitoring = new Monitoring(bookDepthEventHandler, disruptor)) {
            monitoring.start();
            disruptor.start();
            webSocketClient.start();
        } finally {
            disruptor.shutdown();
        }
    }
}