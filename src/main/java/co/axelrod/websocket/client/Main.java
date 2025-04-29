package co.axelrod.websocket.client;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.disruptor.DisruptorFactory;
import co.axelrod.websocket.client.disruptor.event.BookDepthEvent;
import co.axelrod.websocket.client.disruptor.event.BookDepthEventHandler;
import co.axelrod.websocket.client.monitoring.Monitoring;
import co.axelrod.websocket.client.netty.WebSocketClient;
import com.lmax.disruptor.dsl.Disruptor;

public class Main {
    public static void main(String[] args) throws Exception {
        BookDepthEventHandler bookDepthEventHandler = new BookDepthEventHandler();
        Disruptor<BookDepthEvent> disruptor = DisruptorFactory.getDisruptor(bookDepthEventHandler);

        try (WebSocketClient webSocketClient = new WebSocketClient(disruptor, Configuration.BINANCE_WS_URI);
             Monitoring monitoring = new Monitoring(bookDepthEventHandler, disruptor)) {
            monitoring.start();
            disruptor.start();
            webSocketClient.start();
        } finally {
            disruptor.shutdown();
        }
    }
}