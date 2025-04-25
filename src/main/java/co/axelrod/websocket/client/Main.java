package co.axelrod.websocket.client;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.disruptor.DisruptorFactory;
import co.axelrod.websocket.client.event.PriceEvent;
import co.axelrod.websocket.client.event.PriceEventHandler;
import co.axelrod.websocket.client.monitoring.Monitoring;
import co.axelrod.websocket.client.netty.WebSocketClient;
import com.lmax.disruptor.dsl.Disruptor;
import io.netty.util.ResourceLeakDetector;

import java.util.logging.LogManager;

public class Main {
    public static void main(String[] args) throws Exception {
        PriceEventHandler priceEventHandler = new PriceEventHandler();
        Disruptor<PriceEvent> disruptor = DisruptorFactory.getDisruptor(priceEventHandler);

        WebSocketClient webSocketClient = new WebSocketClient(disruptor, Configuration.BINANCE_WS_URI);

        Monitoring monitoring = new Monitoring(priceEventHandler, webSocketClient, disruptor);
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);

        try {
            monitoring.start();
            disruptor.start();
            webSocketClient.start();
        } finally {
            disruptor.shutdown();
        }
    }
}