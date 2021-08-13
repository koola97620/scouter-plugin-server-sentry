package scouter.plugin.server.sentry.xlog;

public class ErrorCountOverThresholdInTime extends RuntimeException {
    public ErrorCountOverThresholdInTime(String msg) {
        super(msg);
    }
}
