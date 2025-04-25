package co.axelrod.websocket.client.config;

import java.net.URI;

public class Configuration {
    public static final URI BINANCE_WS_URI = URI.create("wss://stream.binance.com:443/ws/btcusdt@trade");
    public static final int WS_MAX_CONTENT_LENGTH = 8192;
    public static final int WS_MAX_FRAME_PAYLOAD_LENGTH = 65536;
}
