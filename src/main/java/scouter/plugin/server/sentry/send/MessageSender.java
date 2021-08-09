package scouter.plugin.server.sentry.send;

public interface MessageSender {
    void send(RuntimeException message);
}
