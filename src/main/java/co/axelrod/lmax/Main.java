package co.axelrod.lmax;

import co.axelrod.lmax.config.Configuration;
import co.axelrod.lmax.event.PriceEvent;
import co.axelrod.lmax.event.PriceEventHandler;
import co.axelrod.lmax.netty.WebSocketChannelInitializer;
import co.axelrod.lmax.netty.WebSocketClientHandler;
import co.axelrod.lmax.util.MemoryUtils;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        int bufferSize = 1024;

        Disruptor<PriceEvent> disruptor = new Disruptor<>(PriceEvent::new, bufferSize, DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith(new PriceEventHandler());
        disruptor.start();

        URI uri = new URI(Configuration.BINANCE_WS_URI);
        String host = uri.getHost();
        int port = uri.getPort();

        WebSocketClientHandler webSocketClientHandler = new WebSocketClientHandler(disruptor.getRingBuffer());
        WebSocketChannelInitializer webSocketChannelInitializer = new WebSocketChannelInitializer(uri, webSocketClientHandler);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(webSocketChannelInitializer);
            Channel ch = b.connect(host, port).sync().channel();

            // Print memory usage
            ch.eventLoop().schedule(() -> MemoryUtils.printMemoryUsage(), 15, TimeUnit.SECONDS);
            ch.eventLoop().schedule(() -> MemoryUtils.printMemoryUsage(), 30, TimeUnit.SECONDS);
            ch.eventLoop().schedule(() -> MemoryUtils.printMemoryUsage(), 45, TimeUnit.SECONDS);
            ch.eventLoop().schedule(() -> MemoryUtils.printMemoryUsage(), 60, TimeUnit.SECONDS);

            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
            disruptor.shutdown();
        }
    }
}