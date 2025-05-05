package co.axelrod.websocket.client;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.core.disruptor.DisruptorFactory;
import co.axelrod.websocket.client.core.event.BookDepthEvent;
import co.axelrod.websocket.client.core.event.BookDepthEventHandler;
import co.axelrod.websocket.client.util.monitoring.Monitoring;
import co.axelrod.websocket.client.integration.netty.WebSocketClient;
import co.axelrod.websocket.client.integration.provider.binance.BinanceWebSocketClient;
import co.axelrod.websocket.client.subscription.InstrumentsManager;
import com.lmax.disruptor.dsl.Disruptor;

public class Application {
    public static void main(String[] args) {
        for (String arg : args) {
            processPrintParsedOutputFlag(arg);
        }

        BookDepthEventHandler bookDepthEventHandler = new BookDepthEventHandler();
        Disruptor<BookDepthEvent> disruptor = DisruptorFactory.getDisruptor(bookDepthEventHandler);

        InstrumentsManager instrumentsManager = new InstrumentsManager();

        WebSocketClient webSocketClient = new BinanceWebSocketClient(disruptor, instrumentsManager);
        Monitoring monitoring = new Monitoring(bookDepthEventHandler, disruptor);

        monitoring.start();
        disruptor.start();
        webSocketClient.start();
    }

    private static void processPrintParsedOutputFlag(String arg) {
        if (arg.startsWith("--print-parsed-output")) {
            String[] parts = arg.substring(2).split("=", 2);
            String value = parts[1];
            if ("false".equalsIgnoreCase(value)) {
                Configuration.PRINT_PARSED_OUTPUT = false;
            }
        }
    }
}