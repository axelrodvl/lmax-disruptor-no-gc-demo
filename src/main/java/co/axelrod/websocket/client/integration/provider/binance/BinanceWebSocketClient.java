package co.axelrod.websocket.client.integration.provider.binance;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.core.event.BookDepthEvent;
import co.axelrod.websocket.client.integration.netty.WebSocketClient;
import co.axelrod.websocket.client.subscription.InstrumentsConfigurationListener;
import co.axelrod.websocket.client.subscription.InstrumentsManager;
import com.lmax.disruptor.dsl.Disruptor;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BinanceWebSocketClient extends WebSocketClient implements InstrumentsConfigurationListener {
    private final InstrumentsManager instrumentsManager;
    private Set<String> subscribedInstruments;

    private int requestCount = 0;

    public BinanceWebSocketClient(Disruptor<BookDepthEvent> disruptor, InstrumentsManager instrumentsManager) {
        super(disruptor, buildURI(instrumentsManager));
        this.instrumentsManager = instrumentsManager;
        instrumentsManager.addConfigurationListener(this);
        this.subscribedInstruments = new HashSet<>(instrumentsManager.getInstruments());
    }

    /**
     * {
     *   "method": "SUBSCRIBE",
     *   "params": [
     *     "btcusdt@aggTrade",
     *     "btcusdt@depth"
     *   ],
     *   "id": 1
     * }
     */
    public void subscribe(Set<String> instruments) {
        String params = formatInstrumentsForSubscription(instruments);

        WebSocketFrame webSocketFrame = new TextWebSocketFrame("""
                {
                  "method": "SUBSCRIBE",
                  "params": [%s],
                  "id": %d
                }
                """.formatted(params, requestCount++));

        send(webSocketFrame);
    }

    /**
     * {
     *   "method": "UNSUBSCRIBE",
     *   "params": [
     *     "btcusdt@aggTrade",
     *     "btcusdt@depth"
     *   ],
     *   "id": 1
     * }
     */
    public void unsubscribe(Set<String> instruments) {
        String params = formatInstrumentsForSubscription(instruments);

        WebSocketFrame webSocketFrame = new TextWebSocketFrame("""
                {
                  "method": "UNSUBSCRIBE",
                  "params": [%s],
                  "id": %d
                }
                """.formatted(params, requestCount++));

        send(webSocketFrame);
    }

    private static String formatInstrumentsForSubscription(Set<String> instruments) {
        return instruments.stream()
                .map(BinanceWebSocketClient::formatInstrumentDepth)
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(","));
    }

    @Override
    public void onConfigurationChange() {
        if (instrumentsManager.getInstruments().equals(subscribedInstruments)) {
            return;
        }

        Set<String> addedInstruments = new HashSet<>(instrumentsManager.getInstruments());
        addedInstruments.removeAll(subscribedInstruments);
        if (!addedInstruments.isEmpty()) {
            subscribe(addedInstruments);
        }

        Set<String> removedInstruments = new HashSet<>(subscribedInstruments);
        removedInstruments.removeAll(instrumentsManager.getInstruments());
        if (!removedInstruments.isEmpty()) {
            unsubscribe(removedInstruments);
        }

        subscribedInstruments = new HashSet<>(instrumentsManager.getInstruments());
    }

    private static URI buildURI(InstrumentsManager instrumentsManager) {
        String binanceStreamNames = instrumentsManager.getInstruments().stream()
                .map(BinanceWebSocketClient::formatInstrumentDepth)
                .collect(Collectors.joining(Configuration.BINANCE_INSTRUMENT_DELIMITER));

        return URI.create(Configuration.BINANCE_WS_URI + binanceStreamNames);
    }

    /**
     * <a href="https://developers.binance.com/docs/binance-spot-api-docs/web-socket-streams#partial-book-depth-streams">Depths based on Binance Docs (only 5, 10, and 20)</a>
     */
    public static String formatInstrumentDepth(String instrument) {
        assert Configuration.MARKET_DEPTH == 5 || Configuration.MARKET_DEPTH == 10 || Configuration.MARKET_DEPTH == 20;
        return instrument + "@depth" + Configuration.MARKET_DEPTH;
    }
}
