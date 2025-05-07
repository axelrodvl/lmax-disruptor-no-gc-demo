package co.axelrod.websocket.client.integration.netty;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.core.event.BookDepthEvent;
import co.axelrod.websocket.client.lifecycle.Startable;
import co.axelrod.websocket.client.util.logging.ConsoleWriter;
import com.lmax.disruptor.dsl.Disruptor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

import java.net.URI;

public abstract class WebSocketClient implements Startable {
    private final URI uri;
    private final WebSocketChannelInitializer webSocketChannelInitializer;

    private WebSocketClientHandler webSocketClientHandler;
    private EventLoopGroup group;
    private Channel channel;
    private Bootstrap bootstrap;

    public WebSocketClient(Disruptor<BookDepthEvent> disruptor, URI uri) {
        this.uri = uri;
        this.webSocketClientHandler = new WebSocketClientHandler(this, disruptor.getRingBuffer());
        this.webSocketChannelInitializer = new WebSocketChannelInitializer(uri, webSocketClientHandler);

        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

        IoHandlerFactory ioHandlerFactory = NioIoHandler.newFactory();
        group = new MultiThreadIoEventLoopGroup(ioHandlerFactory);

        bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(uri.getHost(), uri.getPort())
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(getHandlers());
    }

    private ChannelInitializer<SocketChannel> getHandlers() {
        return new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(
                        new IdleStateHandler(Configuration.READ_TIMEOUT_IN_SECONDS, 0, 0),
                        webSocketChannelInitializer
                );
            }
        };
    }

    public void send(WebSocketFrame frame) {
        channel.writeAndFlush(frame);
    }

    public void start() {
        bootstrap.connect()
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        channel = future.channel();
                        channel.closeFuture().addListener((ChannelFutureListener) future1 -> {
                            ConsoleWriter.writeWithNewLine("Connection closed");
                        });
                    } else {
                        ConsoleWriter.writeWithNewLine("Failed to connect:" + future.cause());
                    }
                });
    }

    @Override
    public void stop() throws Exception {
        if (channel != null) {
            channel.writeAndFlush(new CloseWebSocketFrame())
                    .addListener(ChannelFutureListener.CLOSE);
        }
        if (group != null) {
            group.shutdownGracefully().sync();
        }
    }

    public URI getUri() {
        return uri;
    }
}
