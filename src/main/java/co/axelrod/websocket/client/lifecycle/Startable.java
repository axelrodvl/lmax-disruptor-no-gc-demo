package co.axelrod.websocket.client.lifecycle;

public interface Startable extends AutoCloseable {
    void start() throws Exception;
    void stop() throws Exception;

    @Override
    default void close() throws Exception {
        stop();
    }
}
