package co.axelrod.websocket.client.disruptor.event;

import java.util.List;
import java.util.stream.Stream;

public class BookDepth {
    private final int depth;
    private final List<BidAsk> bids;
    private final List<BidAsk> asks;

    private String stream;

    public BookDepth(int depth) {
        this.depth = depth;

        bids = Stream.generate(BidAsk::new)
                .limit(depth)
                .toList();

        asks = Stream.generate(BidAsk::new)
                .limit(depth)
                .toList();
    }

    public int getDepth() {
        return depth;
    }

    public String getStream() {
        return stream;
    }

    public List<BidAsk> getBids() {
        return bids;
    }

    public List<BidAsk> getAsks() {
        return asks;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }
}
