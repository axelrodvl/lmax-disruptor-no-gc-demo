package co.axelrod.lmax;

import co.axelrod.lmax.client.BinanceWebsocketClient;
import co.axelrod.lmax.event.PriceEvent;
import co.axelrod.lmax.event.PriceEventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
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

        WebSocketClient client = new WebSocketClient();
        BinanceWebsocketClient socket = new BinanceWebsocketClient(disruptor.getRingBuffer());

        try {
            client.start();
            URI uri = new URI(BinanceWebsocketClient.URI);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, uri, request);

            TimeUnit.MINUTES.sleep(1);
        } finally {
            client.stop();
        }
    }

}