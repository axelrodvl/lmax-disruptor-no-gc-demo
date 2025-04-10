package co.axelrod.lmax;

import co.axelrod.lmax.jetty.WebSocketSessionListener;
import co.axelrod.lmax.config.Configuration;
import co.axelrod.lmax.event.PriceEvent;
import co.axelrod.lmax.event.PriceEventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static co.axelrod.lmax.util.MemoryUtils.printMemoryUsage;

public class Main {
    public static void main(String[] args) throws Exception {
        printMemoryUsage();

        int bufferSize = 1024;

        Disruptor<PriceEvent> disruptor = new Disruptor<>(PriceEvent::new, bufferSize, DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith(new PriceEventHandler());
        disruptor.start();

        // Instantiate and configure HttpClient.
        HttpClient httpClient = new HttpClient();
        // For example, configure a proxy.
        // httpClient.getProxyConfiguration().addProxy(new HttpProxy("localhost", 8888));

        // Instantiate WebSocketClient, passing HttpClient to the constructor.
        WebSocketClient webSocketClient = new WebSocketClient(httpClient);
        // Configure WebSocketClient, for example:
        webSocketClient.setMaxTextMessageSize(8 * 1024);

        // Start WebSocketClient; this implicitly starts also HttpClient.
        try {
            webSocketClient.start();

            WebSocketSessionListener listener = new WebSocketSessionListener(disruptor.getRingBuffer());
            URI uri = new URI(Configuration.BINANCE_WS_URI);

            ClientUpgradeRequest upgradeRequest = new ClientUpgradeRequest();
            webSocketClient.connect(listener, uri, upgradeRequest);

            TimeUnit.MINUTES.sleep(1);
        } finally {
            webSocketClient.stop();
        }
    }
}