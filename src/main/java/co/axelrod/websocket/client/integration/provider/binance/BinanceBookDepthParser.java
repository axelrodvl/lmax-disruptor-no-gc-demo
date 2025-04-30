package co.axelrod.websocket.client.integration.provider.binance;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.core.model.BidAsk;
import co.axelrod.websocket.client.core.model.BookDepth;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.wire.JSONWire;

import java.nio.ByteBuffer;
import java.util.List;

public class BinanceBookDepthParser {
    private static final Bytes<ByteBuffer> BYTES = Bytes.elasticByteBuffer(Configuration.WS_MAX_CONTENT_LENGTH);
    private static final JSONWire WIRE = new JSONWire(BYTES);
    private static final BinancePartialBookDepth PARSED = new BinancePartialBookDepth();

    public static void parseBookDepth(ByteBuffer source, BookDepth target) {
        WIRE.bytes().clear();
        int remaining = source.remaining();

        for (int i = 0; i < remaining; i++) {
            BYTES.writeByte(source.get());
        }

        BinancePartialBookDepth parsed = new BinancePartialBookDepth();
        WIRE.getValueIn().object(parsed, BinancePartialBookDepth.class);

        target.setStream(parsed.getStream());
        for (int level = 0; level < Configuration.MARKET_DEPTH; level++) {
            setBidAsk(parsed, target, level);
        }
    }

    private static void setBidAsk(BinancePartialBookDepth parsed, BookDepth target, int level) {
        double priceLevel;
        double quantity;

        List<String> bid = parsed.getData().getBids().get(level);

        priceLevel = Bytes.from(bid.getFirst()).parseDouble();
        quantity = Bytes.from(bid.getLast()).parseDouble();

//        priceLevel = Double.parseDouble(bid.getFirst());
//        quantity = Double.parseDouble(bid.getLast());

        BidAsk targetBid = target.getBids().get(level);

        targetBid.setPriceLevel(priceLevel);
        targetBid.setQuantity(quantity);

        List<String> ask = parsed.getData().getAsks().get(level);

//        priceLevel = Double.parseDouble(ask.getFirst());
//        quantity = Double.parseDouble(ask.getLast());

        priceLevel = Bytes.from(ask.getFirst()).parseDouble();
        quantity = Bytes.from(ask.getLast()).parseDouble();

        BidAsk targetAsk = target.getAsks().get(level);
        targetAsk.setPriceLevel(priceLevel);
        targetAsk.setQuantity(quantity);
    }
}
