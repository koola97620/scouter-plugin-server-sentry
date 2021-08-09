package scouter.plugin.server.sentry.performance;

public class GcTimeOverThreshold extends RuntimeException {
    public GcTimeOverThreshold(String msg) {
        super(msg);
    }
}
