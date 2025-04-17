package co.axelrod.websocket.client;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.disruptor.DisruptorFactory;
import co.axelrod.websocket.client.event.PriceEvent;
import co.axelrod.websocket.client.netty.WebSocketClient;
import com.lmax.disruptor.dsl.Disruptor;

public class Main {
    public static void main(String[] args) throws Exception {
        Disruptor<PriceEvent> disruptor = DisruptorFactory.getDisruptor();
        WebSocketClient webSocketClient = new WebSocketClient(disruptor, Configuration.BINANCE_WS_URI);

        try {
            disruptor.start();
            webSocketClient.start();
        } finally {
            disruptor.shutdown();
        }
    }
}