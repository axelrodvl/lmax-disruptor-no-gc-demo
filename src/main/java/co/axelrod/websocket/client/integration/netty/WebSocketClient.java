package co.axelrod.websocket.client.integration.netty;

import co.axelrod.websocket.client.core.event.BookDepthEvent;
import co.axelrod.websocket.client.lifecycle.Startable;
import com.lmax.disruptor.dsl.Disruptor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

import java.net.URI;

public abstract class WebSocketClient implements Startable {
    private final URI uri;
    private final WebSocketChannelInitializer webSocketChannelInitializer;

    WebSocketClientHandler webSocketClientHandler;
    private EventLoopGroup group;
    private Channel channel;

    public WebSocketClient(Disruptor<BookDepthEvent> disruptor, URI uri) {
        this.uri = uri;
        this.webSocketClientHandler = new WebSocketClientHandler(disruptor.getRingBuffer());
        this.webSocketChannelInitializer = new WebSocketChannelInitializer(uri, webSocketClientHandler);
    }

    public void send(WebSocketFrame frame) {
        channel.writeAndFlush(frame);
    }

    public void start() throws Exception {
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

        IoHandlerFactory ioHandlerFactory = NioIoHandler.newFactory();
        group = new MultiThreadIoEventLoopGroup(ioHandlerFactory);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(webSocketChannelInitializer);
        channel = bootstrap.connect(uri.getHost(), uri.getPort())
                .sync()
                .channel();

        channel.closeFuture().sync();
    }

    @Override
    public void stop() throws Exception {
        if (channel != null) {
            channel.writeAndFlush(new CloseWebSocketFrame()).sync();
            channel.closeFuture().sync();
        }
        if (group != null) {
            group.shutdownGracefully().sync();
        }
    }
}
