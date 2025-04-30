package co.axelrod.websocket.client.integration.netty;

import co.axelrod.websocket.client.config.Configuration;
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


    private final URI uri;
    private final WebSocketClientHandler webSocketClientHandler;

    public WebSocketChannelInitializer(URI uri, WebSocketClientHandler webSocketClientHandler) {
        this.uri = uri;
        this.webSocketClientHandler = webSocketClientHandler;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        final SslContext sslCtx = SslContextBuilder.forClient()
                .sslProvider(SslProvider.OPENSSL_REFCNT)
                .build();

        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(sslCtx.newHandler(channel.alloc(), uri.getHost(), uri.getPort()));
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(Configuration.WS_MAX_CONTENT_LENGTH));
        pipeline.addLast(new WebSocketClientProtocolHandler(
                uri,
                WebSocketVersion.V13,
                null,
                true,
                new DefaultHttpHeaders(),
                Configuration.WS_MAX_FRAME_PAYLOAD_LENGTH
        ));
        pipeline.addLast(webSocketClientHandler);
    }
}
