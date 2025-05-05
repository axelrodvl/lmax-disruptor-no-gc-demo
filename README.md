Example of a WebSocket client using LMAX Disruptor with No GC (EpsilonGC)

# Used API
- Subscribing: https://developers.binance.com/docs/binance-spot-api-docs/web-socket-streams#subscribe-to-a-stream
- Unsubscribing: https://developers.binance.com/docs/binance-spot-api-docs/web-socket-streams#unsubscribe-to-a-stream
- Stream: https://developers.binance.com/docs/binance-spot-api-docs/web-socket-streams#partial-book-depth-streams

## Stream frame JSON example
```
{
    "stream": "btcusdt@depth10",
    "data": {
        "lastUpdateId": 67728577194,
        "bids": [
            [
                "93767.99000000",
                "5.98991000"
            ],
            [
                "93767.98000000",
                "0.00006000"
            ],
            ...8 more (depth10)...
        ],
        "asks": [
            [
                "93768.00000000",
                "1.24200000"
            ],
            [
                "93768.01000000",
                "0.00115000"
            ],
            ...8 more (depth10)...
        ]
    }
}
```

# Features
- LMAX Disruptor
- Netty WebSocket Client
- Chronicle Wire

# Threads
- multiThreadIoEventLoopGroup
- Thread-1
- main

# Memory Allocation Events (TODO)
- 5.74 MB [io.netty.internal.tcnative] NioIoHandler -> SSLTask -> CertificateVerifierTask

# Output sample
```
---------------------------------------------------
Handled events: 1416
---------------------------------------------------
Disruptor buffer size: 16
---------------------------------------------------
Used heap memory: 0 KB
Used direct memory: 4096 KB
---------------------------------------------------
Allocated memory (totalMemory): 65536 KB
Maximum memory (maxMemory): 65536 KB
Free memory: 9609 KB (0 KB)
Used memory: 55926 KB (0 KB)
---------------------------------------------------
```

# JSON Parser performance
Zero GC.
About 0.001 MB/sec and 0.00001 B per operation during parsing.

## BinanceBookDepthParserBenchmark
```
Benchmark                                                           Mode  Cnt      Score     Error   Units
BinanceBookDepthParserBenchmark.parseBookDepth                     thrpt   25  70508.455 ± 630.741  ops/ms
BinanceBookDepthParserBenchmark.parseBookDepth:gc.alloc.rate       thrpt   25     ≈ 10⁻³            MB/sec
BinanceBookDepthParserBenchmark.parseBookDepth:gc.alloc.rate.norm  thrpt   25     ≈ 10⁻⁵              B/op
BinanceBookDepthParserBenchmark.parseBookDepth:gc.count            thrpt   25        ≈ 0            counts
```

# TODO
- Configurable Reconnect
