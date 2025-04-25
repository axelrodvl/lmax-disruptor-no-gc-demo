package co.axelrod.websocket.client.netty;

import co.axelrod.websocket.client.event.PriceEvent;
import com.lmax.disruptor.dsl.Disruptor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

import java.net.URI;

public class WebSocketClient {
    private final URI uri;
    private final WebSocketChannelInitializer webSocketChannelInitializer;

    public WebSocketClient(Disruptor<PriceEvent> disruptor, URI uri) {
        this.uri = uri;
        WebSocketClientHandler webSocketClientHandler = new WebSocketClientHandler(disruptor.getRingBuffer());
        this.webSocketChannelInitializer = new WebSocketChannelInitializer(uri, webSocketClientHandler);
    }

    public void start() throws InterruptedException {
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

        IoHandlerFactory ioHandlerFactory = NioIoHandler.newFactory();

        try (EventLoopGroup group = new MultiThreadIoEventLoopGroup(ioHandlerFactory)) {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(webSocketChannelInitializer);
            Channel channel = bootstrap.connect(uri.getHost(), uri.getPort())
                    .sync()
                    .channel();

            channel.closeFuture().sync();
        }
    }
}
