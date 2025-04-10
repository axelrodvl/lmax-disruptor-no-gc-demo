package co.axelrod.lmax.event;

import com.lmax.disruptor.EventHandler;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PriceEventHandler implements EventHandler<PriceEvent> {
//    private static final Logger log = LoggerFactory.getLogger(PriceEventHandler.class);

    @Override
    public void onEvent(PriceEvent event, long sequence, boolean endOfBatch) {
//        log.info("Event handled");
//        System.out.println("Event handled");

//        ByteBuffer copy = event.getValue().duplicate();
//        log.info("Event handled: {}", StandardCharsets.UTF_8.decode(copy));
//        System.out.println("Event handled: " + StandardCharsets.UTF_8.decode(copy));
    }
}