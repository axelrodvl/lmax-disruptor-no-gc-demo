package co.axelrod.websocket.client.subscription;

import co.axelrod.websocket.client.config.Configuration;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class InstrumentsManager {
    private final Set<InstrumentsConfigurationListener> listeners;
    private final Set<String> instruments;

    public InstrumentsManager() {
        this.listeners = new HashSet<>();
        this.instruments = new CopyOnWriteArraySet<>(Configuration.DEFAULT_INSTRUMENTS);
    }

    public void addConfigurationListener(InstrumentsConfigurationListener listener) {
        this.listeners.add(listener);
    }

    public void removeConfigurationListener(InstrumentsConfigurationListener listener) {
        this.listeners.remove(listener);
    }

    public Set<String> getInstruments() {
        return instruments;
    }

    public void subscribe(Set<String> instrumentsToSubscribe) {
        instruments.addAll(instrumentsToSubscribe);
        listeners.forEach(InstrumentsConfigurationListener::onConfigurationChange);
    }

    public void subscribe(String instrumentToSubscribe) {
        instruments.add(instrumentToSubscribe);
        listeners.forEach(InstrumentsConfigurationListener::onConfigurationChange);
    }

    public void unsubscribe(Set<String> instrumentToUnsubscribe) {
        instruments.removeAll(instrumentToUnsubscribe);
        listeners.forEach(InstrumentsConfigurationListener::onConfigurationChange);
    }

    public void unsubscribe(String instrumentToUnsubscribe) {
        instruments.remove(instrumentToUnsubscribe);
        listeners.forEach(InstrumentsConfigurationListener::onConfigurationChange);
    }
}
