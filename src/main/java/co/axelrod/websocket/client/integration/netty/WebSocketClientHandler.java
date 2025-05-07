package co.axelrod.websocket.client.integration.netty;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.core.event.BookDepthEvent;
import co.axelrod.websocket.client.util.logging.ConsoleWriter;
import com.lmax.disruptor.RingBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private final WebSocketClient webSocketClient;
    private final RingBuffer<BookDepthEvent> ringBuffer;

    long startTime = -1;

    public WebSocketClientHandler(WebSocketClient webSocketClient, RingBuffer<BookDepthEvent> ringBuffer) {
        this.webSocketClient = webSocketClient;
        this.ringBuffer = ringBuffer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (startTime < 0) {
            startTime = System.currentTimeMillis();
        }
        ConsoleWriter.writeWithNewLine("Connected to: " + ctx.channel().remoteAddress());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            ConsoleWriter.writeWithNewLine("Handshake completed, WebSocket connected");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof TextWebSocketFrame frame) {
            ByteBuf nettyByteBuf = frame.content().retain();

            if (isControlMessageResponse(nettyByteBuf)) {
                nettyByteBuf.release();
                return;
            }

            long sequenceNumber = ringBuffer.next();
            try {
                BookDepthEvent event = ringBuffer.get(sequenceNumber);

                ByteBuffer eventByteBuffer = event.getByteBuffer();
                eventByteBuffer.clear();
                eventByteBuffer.limit(nettyByteBuf.readableBytes());

                nettyByteBuf.getBytes(nettyByteBuf.readerIndex(), eventByteBuffer);
                eventByteBuffer.flip();
            } finally {
                ringBuffer.publish(sequenceNumber);
                nettyByteBuf.release();
            }
        } else if (msg instanceof CloseWebSocketFrame) {
            ConsoleWriter.writeWithNewLine("Received close frame, shutting down");
            ctx.channel().close();
        }
    }

    private boolean isControlMessageResponse(ByteBuf nettyByteBuf) {
        int readerIndex = nettyByteBuf.readerIndex();
        int readable = nettyByteBuf.readableBytes();

        final String prefix = "{\"result\"";
        int prefixLen = prefix.length();

        if (readable < prefixLen) {
            return false;
        }

        for (int i = 0; i < prefixLen; i++) {
            if (nettyByteBuf.getByte(readerIndex + i) != (byte) prefix.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        ConsoleWriter.writeWithNewLine("Disconnected from: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) {
        ConsoleWriter.writeWithNewLine("Sleeping for: " + Configuration.RECONNECT_DELAY_IN_SECONDS + " s");

        ctx.channel().eventLoop().schedule(() -> {
            ConsoleWriter.writeWithNewLine("Reconnecting to " + webSocketClient.getUri());
            webSocketClient.start();
        }, Configuration.RECONNECT_DELAY_IN_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}