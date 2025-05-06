package co.axelrod.websocket.client.integration.provider.binance;

import co.axelrod.websocket.client.config.Configuration;
import co.axelrod.websocket.client.core.disruptor.DisruptorFactory;
import co.axelrod.websocket.client.core.event.BookDepthEvent;
import co.axelrod.websocket.client.core.event.BookDepthEventHandler;
import co.axelrod.websocket.client.core.event.TestInstrumentCounterHandler;
import co.axelrod.websocket.client.subscription.InstrumentsManager;
import com.lmax.disruptor.dsl.Disruptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

public class BinanceWebSocketClientTest {
    private InstrumentsManager instrumentsManager;
    private BinanceWebSocketClient webSocketClient;
    private Disruptor<BookDepthEvent> disruptor;

    private BookDepthEventHandler bookDepthEventHandler;
    private TestInstrumentCounterHandler testInstrumentCounterHandler;

    @BeforeEach
    public void setUp() {
        bookDepthEventHandler = new BookDepthEventHandler();
        disruptor = DisruptorFactory.getDisruptor(bookDepthEventHandler);

        testInstrumentCounterHandler = new TestInstrumentCounterHandler();
        disruptor.after(bookDepthEventHandler).then(testInstrumentCounterHandler);

        instrumentsManager = new InstrumentsManager();

        webSocketClient = new BinanceWebSocketClient(disruptor, instrumentsManager);

        disruptor.start();
        webSocketClient.start();

        awaitClientStarted();
    }

    @AfterEach
    public void tearDown() throws Exception {
        webSocketClient.stop();
        disruptor.shutdown();
    }

    @Test
    public void clientStarts() {
        assertTrue(bookDepthEventHandler.getEventCount() > 0);
    }

    @Test
    public void testAddInstrument() {
        // given
        String bnbBtcStream = BinanceWebSocketClient.formatInstrumentDepth(TestInstruments.BNB_BTC);
        assertFalse(testInstrumentCounterHandler.getInstrumentOccurrence().containsKey(bnbBtcStream));

        // when
        instrumentsManager.subscribe(TestInstruments.BNB_BTC);

        // then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            assertTrue(instrumentsManager.getInstruments().contains(TestInstruments.BNB_BTC));
            assertTrue(testInstrumentCounterHandler.getInstrumentOccurrence().containsKey(bnbBtcStream));
            assertTrue(testInstrumentCounterHandler.getInstrumentOccurrence().get(bnbBtcStream) > 0);
        });
    }

    @Test
    public void testRemoveInstrument() {
        // given
        String btcUsdtStream = BinanceWebSocketClient.formatInstrumentDepth(TestInstruments.BTC_USDT);
        assertTrue(testInstrumentCounterHandler.getInstrumentOccurrence().containsKey(btcUsdtStream));

        // when
        instrumentsManager.unsubscribe(TestInstruments.BTC_USDT);
        int occurrence = testInstrumentCounterHandler.getInstrumentOccurrence().get(btcUsdtStream);

        // then
        await().atMost(5, SECONDS).untilAsserted(() -> {
            assertFalse(instrumentsManager.getInstruments().contains(TestInstruments.BTC_USDT));
            assertTrue(testInstrumentCounterHandler.getInstrumentOccurrence().containsKey(btcUsdtStream));
            assertEquals(occurrence, testInstrumentCounterHandler.getInstrumentOccurrence().get(btcUsdtStream));
        });
    }

    @Test
    public void testStartStopClient() throws Exception {
        // given
        webSocketClient.stop();
        await().pollInterval(1, SECONDS)
                .atMost(3, SECONDS)
                .until(() -> {
                    int oldEventCountAfterStop = bookDepthEventHandler.getEventCount();
                    Thread.sleep(1000);
                    int newEventCountAfterStop = bookDepthEventHandler.getEventCount();
                    return oldEventCountAfterStop == newEventCountAfterStop;
                });

        // when
        int oldEventCountAfterStop = bookDepthEventHandler.getEventCount();
        webSocketClient.start();

        // then
        await().atMost(3, SECONDS)
                .until(() -> bookDepthEventHandler.getEventCount() > oldEventCountAfterStop);
    }

    @Test
    public void reconnect() {
        // TODO
    }

    private void awaitClientStarted() {
        Consumer<String> instrumentReceived = formattedInstrument ->
                assertTrue(
                        testInstrumentCounterHandler.getInstrumentOccurrence()
                                .containsKey(formattedInstrument)
                );

        await().atMost(5, SECONDS).untilAsserted(() -> {
            Configuration.DEFAULT_INSTRUMENTS.stream()
                    .map(BinanceWebSocketClient::formatInstrumentDepth)
                    .forEach(instrumentReceived);

            assertTrue(testInstrumentCounterHandler.getInstrumentOccurrence().values().stream()
                    .allMatch(occurrence -> occurrence > 0)
            );
        });
    }
}
