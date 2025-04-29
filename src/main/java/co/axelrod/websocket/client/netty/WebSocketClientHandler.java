package co.axelrod.websocket.client.netty;

import co.axelrod.websocket.client.disruptor.event.BookDepthEvent;
import co.axelrod.websocket.client.logging.ConsoleWriter;
import com.lmax.disruptor.RingBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;

import java.nio.ByteBuffer;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private final RingBuffer<BookDepthEvent> ringBuffer;

    public WebSocketClientHandler(RingBuffer<BookDepthEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}