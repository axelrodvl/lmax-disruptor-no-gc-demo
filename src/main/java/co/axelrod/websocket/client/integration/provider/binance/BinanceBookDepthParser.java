package co.axelrod.websocket.client.integration.provider.binance;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.core.model.BookDepth;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.wire.JSONWire;
import net.openhft.chronicle.wire.ValueIn;

import java.nio.ByteBuffer;
import java.util.function.BiConsumer;

/**
 * <a href="https://developers.binance.com/docs/binance-spot-api-docs/web-socket-streams#partial-book-depth-streams">Binance - Partial Book Depth Streams Payload</a>
 */
public class BinanceBookDepthParser {
    private static final String DATA_FIELD = "data";
    private static final String STREAM_FIELD = "stream";
    private static final String BIDS_FIELD = "bids";
    private static final String ASKS_FIELD = "asks";

    private static final Bytes<ByteBuffer> BYTES = Bytes.elasticByteBuffer(Configuration.WS_MAX_CONTENT_LENGTH);
    private static final JSONWire WIRE = new JSONWire(BYTES);
    private static final LevelHolder LEVEL_HOLDER = new LevelHolder();

    public static void parseBookDepth(ByteBuffer source, BookDepth target) {
        WIRE.clear();
        WIRE.bytes().clear();
        int remaining = source.remaining();

        for (int i = 0; i < remaining; i++) {
            BYTES.writeByte(source.get());
        }

        target.setStream(WIRE.read(STREAM_FIELD).readString());

        WIRE.read(DATA_FIELD).marshallable(data -> {
            LEVEL_HOLDER.level = 0;
            data.read(BIDS_FIELD).sequence(LEVEL_HOLDER, setBids(target));
            LEVEL_HOLDER.level = 0;
            data.read(ASKS_FIELD).sequence(LEVEL_HOLDER, setAsks(target));
        });
    }

    private static BiConsumer<LevelHolder, ValueIn> setBids(BookDepth bookDepth) {
        return (levelHolder, bids) -> {
            while (bids.hasNextSequenceItem()) {
                bids.sequence(bookDepth, setBid(levelHolder));
                levelHolder.level++;
            }
        };
    }

    private static BiConsumer<BookDepth, ValueIn> setBid(LevelHolder levelHolder) {
        return (bookDepth, bid) -> {
            double priceLevel = bid.readDouble();
            bookDepth.getBids().get(levelHolder.level).setPriceLevel(priceLevel);
            double quantity = bid.readDouble();
            bookDepth.getBids().get(levelHolder.level).setQuantity(quantity);
        };
    }

    private static BiConsumer<LevelHolder, ValueIn> setAsks(BookDepth bookDepth) {
        return (levelHolder, asks) -> {
            while (asks.hasNextSequenceItem()) {
                asks.sequence(bookDepth, setAsk(levelHolder));
                levelHolder.level++;
            }
        };
    }

    private static BiConsumer<BookDepth, ValueIn> setAsk(LevelHolder levelHolder) {
        return (bookDepth, ask) -> {
            double priceLevel = ask.readDouble();
            bookDepth.getAsks().get(levelHolder.level).setPriceLevel(priceLevel);
            double quantity = ask.readDouble();
            bookDepth.getAsks().get(levelHolder.level).setQuantity(quantity);
        };
    }

    private static final class LevelHolder {
        int level;
    }
}
