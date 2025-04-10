package co.axelrod.lmax.jetty;

import co.axelrod.lmax.event.PriceEvent;
import com.lmax.disruptor.RingBuffer;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Frame;
import org.eclipse.jetty.websocket.api.Session;

public class WebSocketSessionListener implements Session.Listener {
    private final RingBuffer<PriceEvent> ringBuffer;

    private Session session;

    public WebSocketSessionListener(RingBuffer<PriceEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    @Override
    public void onWebSocketOpen(Session session)
    {
        // The WebSocket endpoint has been opened.

        // Store the session to be able to send data to the remote peer.
        this.session = session;

        // You may configure the session.
        session.setMaxTextMessageSize(16 * 1024);

        // Demand for more events.
        session.demand();
    }

    @Override
    public void onWebSocketFrame(Frame frame, Callback callback) {
        if (frame.getType() == Frame.Type.TEXT) {
            ringBuffer.publishEvent((event, sequence) -> event.setValue(frame.getPayload()));
        }

        // Complete the callback to release the payload ByteBuffer.
        callback.succeed();
    }

    @Override
    public void onWebSocketError(Throwable cause)
    {
        // The WebSocket endpoint failed.

        // You may log the error.
        cause.printStackTrace();
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        // The WebSocket endpoint has been closed.
    }
}