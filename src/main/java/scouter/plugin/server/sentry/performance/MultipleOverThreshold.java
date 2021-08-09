package scouter.plugin.server.sentry.performance;

public class MultipleOverThreshold extends RuntimeException implements ThresholdException {
    public MultipleOverThreshold(String msg) {
        super(msg);
    }
}
