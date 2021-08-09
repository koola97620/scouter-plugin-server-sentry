package scouter.plugin.server.sentry.performance;

public class ActiveServiceOverThreshold extends RuntimeException {
    public ActiveServiceOverThreshold(String msg) {
        super(msg);
    }
}
