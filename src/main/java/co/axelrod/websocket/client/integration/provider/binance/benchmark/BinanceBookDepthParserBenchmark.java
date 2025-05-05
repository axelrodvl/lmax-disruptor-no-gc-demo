package co.axelrod.websocket.client.integration.provider.binance.benchmark;

import co.axelrod.websocket.client.core.model.BookDepth;
import co.axelrod.websocket.client.integration.provider.binance.BinanceBookDepthParser;
import net.openhft.chronicle.bytes.Bytes;
import org.openjdk.jmh.annotations.*;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class BinanceBookDepthParserBenchmark {
    private ByteBuffer source;
    private BookDepth bookDepth;

    private static final String BINANCE_FRAME_JSON = """
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

    @Setup(Level.Trial)
    public void setup() {
        this.bookDepth = new BookDepth(5);
        this.source = ByteBuffer.wrap(BINANCE_FRAME_JSON.getBytes());
    }

    @Benchmark
    public void parseBookDepth() {
        BinanceBookDepthParser.parseBookDepth(source, bookDepth);
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        // Not required
    }
}
