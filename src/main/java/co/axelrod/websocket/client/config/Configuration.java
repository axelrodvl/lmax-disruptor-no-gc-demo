package co.axelrod.websocket.client.config;

import java.net.URI;
import java.util.Set;

public class Configuration {
    public static final int MARKET_DEPTH = 10;

    public static final Set<String> DEFAULT_INSTRUMENTS = Set.of(
            "btcusdt",
            "ethusdt"
    );

    public static final URI BINANCE_WS_URI = URI.create("wss://stream.binance.com:443/stream?streams=" +
            "btcusdt@depth" +
            Integer.toString(MARKET_DEPTH) +
            "/ethusdt@depth" +
            Integer.toString(MARKET_DEPTH));

    public static final int WS_MAX_CONTENT_LENGTH = 8192;
    public static final int WS_MAX_FRAME_PAYLOAD_LENGTH = 65536;
}
