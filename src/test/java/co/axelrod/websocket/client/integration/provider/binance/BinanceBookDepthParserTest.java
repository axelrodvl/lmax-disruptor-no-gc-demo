package co.axelrod.websocket.client.integration.provider.binance;

import co.axelrod.websocket.client.core.model.BookDepth;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinanceBookDepthParserTest {
    private static final int MARKET_DEPTH = 5;

    String binanceFrameJson = """
            {
                "stream": "ethusdt@depth5",
                "data": {
                    "lastUpdateId": 50424843692,
                    "bids": [
                        [
                            "1812.69000000",
                            "2.43840000"
                        ],
                        [
                            "1812.68000000",
                            "0.66840000"
                        ],
                        [
                            "1812.67000000",
                            "0.08030000"
                        ],
                        [
                            "1812.66000000",
                            "0.03400000"
                        ],
                        [
                            "1812.65000000",
                            "0.01190000"
                        ]
                    ],
                    "asks": [
                        [
                            "1812.70000000",
                            "82.53810000"
                        ],
                        [
                            "1812.71000000",
                            "4.66940000"
                        ],
                        [
                            "1812.72000000",
                            "0.00290000"
                        ],
                        [
                            "1812.73000000",
                            "0.00290000"
                        ],
                        [
                            "1812.74000000",
                            "0.00620000"
                        ]
                    ]
                }
            }
            """;

    @Test
    public void parseBinanceWebSocketFrameBookDepthJson() {
        BookDepth bookDepth = new BookDepth(MARKET_DEPTH);
        ByteBuffer buffer = ByteBuffer.wrap(binanceFrameJson.getBytes());
        BinanceBookDepthParser.parseBookDepth(buffer, bookDepth);

        assertEquals("ethusdt@depth5", bookDepth.getStream());

        assertEquals(1812.69, bookDepth.getBids().getFirst().getPriceLevel());
        assertEquals(2.4384, bookDepth.getBids().getFirst().getQuantity());
        assertEquals(1812.65, bookDepth.getBids().getLast().getPriceLevel());
        assertEquals(0.0119, bookDepth.getBids().getLast().getQuantity());

        assertEquals(1812.7, bookDepth.getAsks().getFirst().getPriceLevel());
        assertEquals(82.5381, bookDepth.getAsks().getFirst().getQuantity());
        assertEquals(1812.74, bookDepth.getAsks().getLast().getPriceLevel());
        assertEquals(0.0062, bookDepth.getAsks().getLast().getQuantity());
    }
}