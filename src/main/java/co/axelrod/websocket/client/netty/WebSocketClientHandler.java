package co.axelrod.websocket.client.netty;

import co.axelrod.websocket.client.event.PriceEvent;
import co.axelrod.websocket.client.console.ConsoleWriter;
import com.lmax.disruptor.RingBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private final RingBuffer<PriceEvent> ringBuffer;

    public WebSocketClientHandler(RingBuffer<PriceEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            ConsoleWriter.write("Handshake completed, WebSocket connected");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof TextWebSocketFrame frame) {
            ByteBuf buf = frame.content().retain();
            long sequenceNumber = ringBuffer.next();
            try {
                PriceEvent event = ringBuffer.get(sequenceNumber);
                event.setBuffer(buf);
            } finally {
                ringBuffer.publish(sequenceNumber);
            }
        } else if (msg instanceof CloseWebSocketFrame) {
            ConsoleWriter.write("Received close frame, shutting down");
            ctx.channel().close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}