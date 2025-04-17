package co.axelrod.websocket.client.netty;

import co.axelrod.websocket.client.event.PriceEvent;
import co.axelrod.websocket.client.util.MemoryUtils;
import com.lmax.disruptor.dsl.Disruptor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.IoHandlerFactory;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class WebSocketClient {
    private final URI uri;
    private final WebSocketChannelInitializer webSocketChannelInitializer;

    public WebSocketClient(Disruptor<PriceEvent> disruptor, URI uri) {
        this.uri = uri;
        WebSocketClientHandler webSocketClientHandler = new WebSocketClientHandler(disruptor.getRingBuffer());
        this.webSocketChannelInitializer = new WebSocketChannelInitializer(uri, webSocketClientHandler);
    }

    public void start() throws InterruptedException {
        IoHandlerFactory ioHandlerFactory = NioIoHandler.newFactory();

        try (EventLoopGroup group = new MultiThreadIoEventLoopGroup(ioHandlerFactory)) {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(webSocketChannelInitializer);
            Channel channel = bootstrap.connect(uri.getHost(), uri.getPort())
                    .sync()
                    .channel();

            schedulePrintingMemoryUsageAndClosingChannel(channel);

            channel.closeFuture().sync();
        }
    }

    private static void schedulePrintingMemoryUsageAndClosingChannel(Channel channel) {
        channel.eventLoop().schedule(MemoryUtils::printMemoryUsage, 15, TimeUnit.SECONDS);
        channel.eventLoop().schedule(MemoryUtils::printMemoryUsage, 30, TimeUnit.SECONDS);
        channel.eventLoop().schedule(MemoryUtils::printMemoryUsage, 45, TimeUnit.SECONDS);
        channel.eventLoop().schedule(MemoryUtils::printMemoryUsage, 60, TimeUnit.SECONDS);
        channel.eventLoop().schedule(MemoryUtils::printMemoryUsage, 120, TimeUnit.SECONDS);
        channel.eventLoop().schedule(MemoryUtils::printMemoryUsage, 240, TimeUnit.SECONDS);
        channel.eventLoop().schedule(() -> channel.close(), 240, TimeUnit.SECONDS);
    }
}
