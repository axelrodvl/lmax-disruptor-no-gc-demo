Example of a WebSocket client using LMAX Disruptor with No GC (EpsilonGC) – under 64 MB

# Threads
- multiThreadIoEventLoopGroup
- Thread-1
- main

# Memory Allocation Events
- 5.74 MB [io.netty.internal.tcnative] NioIoHandler -> SSLTask -> CertificateVerifierTask

# TODO
- No GC
- Configurable Reconnect
- Start and stop
- Subscription and unsubscription to a multiple sources
- Modifying subscription list (new tickers)

