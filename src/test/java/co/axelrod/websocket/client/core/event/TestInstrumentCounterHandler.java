package co.axelrod.websocket.client.core.event;

import com.lmax.disruptor.EventHandler;

import java.util.HashMap;
import java.util.Map;

public class TestInstrumentCounterHandler implements EventHandler<BookDepthEvent> {
    private final Map<String, Integer> instrumentOccurrence = new HashMap<>();

    @Override
    public void onEvent(BookDepthEvent event, long sequence, boolean endOfBatch) {
        String stream = event.getBookDepth().getStream();
        instrumentOccurrence.merge(stream, 1, Integer::sum);
    }

    public Map<String, Integer> getInstrumentOccurrence() {
        return instrumentOccurrence;
    }
}
