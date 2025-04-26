Example of a WebSocket client using LMAX Disruptor with No GC (EpsilonGC) – under 64 MB

# Threads
- multiThreadIoEventLoopGroup
- Thread-1
- main

# Memory Allocation Events
- 5.74 MB [io.netty.internal.tcnative] NioIoHandler -> SSLTask -> CertificateVerifierTask

# Output sample
```
---------------------------------------------------
Handled events: 25532
---------------------------------------------------
Disruptor buffer size: 16
---------------------------------------------------
Used heap memory: 0 KB
Used direct memory: 4096 KB
---------------------------------------------------
Allocated memory (totalMemory): 262144 KB
Maximum memory (maxMemory): 262144 KB
Free memory: 210786 KB (0 KB)
Used memory: 51357 KB (0 KB)
---------------------------------------------------
```

# TODO
- No GC
- Configurable Reconnect
- Start and stop
- Subscription and unsubscription to a multiple sources
- Modifying subscription list (new tickers)

