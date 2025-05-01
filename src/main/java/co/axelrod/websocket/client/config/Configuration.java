package co.axelrod.websocket.client.config;

import java.util.Set;

public class Configuration {
    public static final int MARKET_DEPTH = 10;

    public static final Set<String> DEFAULT_INSTRUMENTS = Set.of(
            "btcusdt",
            "ethusdt"
    );

    public static final String BINANCE_WS_URI = "wss://stream.binance.com:443/stream?streams=";
    public static final String BINANCE_INSTRUMENT_DELIMITER = "/";

    public static final int WS_MAX_CONTENT_LENGTH = 8192;
    public static final int WS_MAX_FRAME_PAYLOAD_LENGTH = 65536;
}
