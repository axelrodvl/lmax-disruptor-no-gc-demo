package co.axelrod.websocket.client.parser;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.disruptor.event.BidAsk;
import co.axelrod.websocket.client.disruptor.event.BookDepth;
import co.axelrod.websocket.client.provider.binance.BinancePartialBookDepth;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.wire.JSONWire;

import java.nio.ByteBuffer;
import java.util.List;

public class BinanceBookDepthParser {
    public static void parseBookDepth(ByteBuffer source, BookDepth target) {
        // TODO Rewrite to reuse the same wire?
        JSONWire wire = new JSONWire(Bytes.wrapForRead(source));
        BinancePartialBookDepth parsed = new BinancePartialBookDepth();
        wire.getValueIn().object(parsed, BinancePartialBookDepth.class);

        target.setStream(parsed.getStream());
        for (int level = 0; level < Configuration.MARKET_DEPTH; level++) {
            setBidAsk(parsed, target, level);
        }
    }

    private static void setBidAsk(BinancePartialBookDepth parsed, BookDepth target, int level) {
        double priceLevel;
        double quantity;

        List<String> bid = parsed.getData().getBids().get(level);
        priceLevel = Double.parseDouble(bid.getFirst());
        quantity = Double.parseDouble(bid.getLast());

        BidAsk targetBid = target.getBids().get(level);
        targetBid.setPriceLevel(priceLevel);
        targetBid.setQuantity(quantity);

        List<String> ask = parsed.getData().getAsks().get(level);
        priceLevel = Double.parseDouble(ask.getFirst());
        quantity = Double.parseDouble(ask.getLast());

        BidAsk targetAsk = target.getAsks().get(level);
        targetAsk.setPriceLevel(priceLevel);
        targetAsk.setQuantity(quantity);
    }
}
