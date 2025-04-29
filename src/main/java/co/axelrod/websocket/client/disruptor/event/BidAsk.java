package co.axelrod.websocket.client.disruptor.event;

public class BidAsk {
    private long priceLevel;
    private long quantity;

    public long getPriceLevel() {
        return priceLevel;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setPriceLevel(long priceLevel) {
        this.priceLevel = priceLevel;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
