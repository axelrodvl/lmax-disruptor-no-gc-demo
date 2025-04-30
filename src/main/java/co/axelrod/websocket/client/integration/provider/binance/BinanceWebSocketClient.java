package co.axelrod.websocket.client.integration.provider.binance;

import co.axelrod.websocket.client.core.event.BookDepthEvent;
import co.axelrod.websocket.client.integration.netty.WebSocketClient;
import co.axelrod.websocket.client.subscription.InstrumentsConfigurationListener;
import co.axelrod.websocket.client.subscription.InstrumentsManager;
import com.lmax.disruptor.dsl.Disruptor;

import java.net.URI;
import java.util.Set;

public class BinanceWebSocketClient extends WebSocketClient implements InstrumentsConfigurationListener {
    private final InstrumentsManager instrumentsManager;

    public BinanceWebSocketClient(Disruptor<BookDepthEvent> disruptor, URI uri, InstrumentsManager instrumentsManager) {
        super(disruptor, uri);
        this.instrumentsManager = instrumentsManager;
        instrumentsManager.addConfigurationListener(this);
    }

    public void subscribe(Set<String> instruments) {

    }

    @Override
    public void onConfigurationChanged() {
        subscribe(instrumentsManager.getInstruments());
    }
}
