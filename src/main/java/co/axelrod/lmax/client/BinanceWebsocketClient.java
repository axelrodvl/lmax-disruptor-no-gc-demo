package co.axelrod.lmax.client;

import co.axelrod.lmax.event.PriceEvent;
import com.lmax.disruptor.RingBuffer;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketOpen;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.nio.ByteBuffer;

@WebSocket
public class BinanceWebsocketClient {
    public static final String URI = "wss://stream.binance.com:443/ws/btcusdt@trade";

//    private static final Logger log = LoggerFactory.getLogger(BinanceWebsocketClient.class);

    private final RingBuffer<PriceEvent> ringBuffer;

    public BinanceWebsocketClient(RingBuffer<PriceEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    @OnWebSocketOpen
    public void onConnect(Session session) {
//        log.info("Connected to Binance WebSocket");
        System.out.println("Connected to Binance WebSocket");
    }

    @OnWebSocketFrame
    public void onFrame(Frame frame, Callback callback) {
        if (frame.getType() == Frame.Type.TEXT) {
            ByteBuffer payload = frame.getPayload();
            ringBuffer.publishEvent((event, sequence) -> event.setValue(payload));
        }
        callback.succeed();
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
//        log.info("WebSocket closed: {} - {}", statusCode, reason);
        System.out.println("WebSocket closed: " + statusCode + " - " + reason);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        t.printStackTrace();
    }
}
