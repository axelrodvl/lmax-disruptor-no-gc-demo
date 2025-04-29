package co.axelrod.websocket.client.provider.binance;

import net.openhft.chronicle.wire.Marshallable;

import java.util.List;

/**
 * <a href="https://developers.binance.com/docs/binance-spot-api-docs/web-socket-streams#partial-book-depth-streams">Binance - Partial Book Depth Streams Payload</a>
 */
public class BinancePartialBookDepth implements Marshallable {
    private String stream;
    private Data data;

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data implements Marshallable {
        private long lastUpdateId;
        private List<List<String>> bids;
        private List<List<String>> asks;

        public long getLastUpdateId() {
            return lastUpdateId;
        }

        public void setLastUpdateId(long lastUpdateId) {
            this.lastUpdateId = lastUpdateId;
        }

        public List<List<String>> getBids() {
            return bids;
        }

        public void setBids(List<List<String>> bids) {
            this.bids = bids;
        }

        public List<List<String>> getAsks() {
            return asks;
        }

        public void setAsks(List<List<String>> asks) {
            this.asks = asks;
        }
    }
}
