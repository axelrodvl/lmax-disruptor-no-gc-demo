package co.axelrod.websocket.client.disruptor.event;

public class BidAsk {
    private double priceLevel;
    private double quantity;

    public double getPriceLevel() {
        return priceLevel;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setPriceLevel(double priceLevel) {
        this.priceLevel = priceLevel;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
