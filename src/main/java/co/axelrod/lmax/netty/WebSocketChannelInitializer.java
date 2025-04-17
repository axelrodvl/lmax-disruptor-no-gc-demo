package co.axelrod.lmax.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

import java.net.URI;

public class WebSocketChannelInitializer extends ChannelInitializer<Channel> {
    private static final int MAX_CONTENT_LENGTH = 8192;
    private static final int MAX_FRAME_PAYLOAD_LENGTH = 65536;

    private final URI uri;
    private final WebSocketClientHandler webSocketClientHandler;

    public WebSocketChannelInitializer(URI uri, WebSocketClientHandler webSocketClientHandler) {
        this.uri = uri;
        this.webSocketClientHandler = webSocketClientHandler;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        final SslContext sslCtx = SslContextBuilder.forClient().sslProvider(SslProvider.OPENSSL_REFCNT).build();

        String host = uri.getHost();
        int port = uri.getPort();

        ChannelPipeline p = ch.pipeline();
        p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
        p.addLast(new HttpClientCodec());
        p.addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
        p.addLast(new WebSocketClientProtocolHandler(
                uri,
                WebSocketVersion.V13,
                null,
                true,
                new DefaultHttpHeaders(),
                MAX_FRAME_PAYLOAD_LENGTH
        ));
        p.addLast(webSocketClientHandler);
    }
}
