package co.axelrod.websocket.client.integration.provider.binance.benchmark;

import net.openhft.chronicle.bytes.Bytes;
import org.openjdk.jmh.annotations.*;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class BinanceBookDepthParserBenchmark {
    private Bytes<?> bytes;

    @Setup(Level.Iteration)
    public void setup() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(32);
        buffer.put("12345.6789".getBytes());
        buffer.flip();
        bytes = Bytes.wrapForRead(buffer);
    }

    @Benchmark
    public double parseDoubleNoGc() {
        bytes.readPosition(0); // reset to start
        return bytes.parseDouble();
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        bytes.releaseLast();
    }
}
