package scouter.plugin.server.sentry.performance;

public class TpsOverThreshold extends RuntimeException {
    public TpsOverThreshold(String msg) {
        super(msg);
    }
}
