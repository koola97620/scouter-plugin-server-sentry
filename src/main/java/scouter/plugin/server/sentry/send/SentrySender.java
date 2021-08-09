package scouter.plugin.server.sentry.send;

import io.sentry.Sentry;
import scouter.server.Logger;

public class SentrySender implements MessageSender {
    private String sentryDsn;

    public SentrySender(String sentryDsn) {
        this.sentryDsn = sentryDsn;
    }

    @Override
    public void send(RuntimeException exception) {
        Sentry.init(sentryDsn);
        Sentry.init(options -> {
            options.setDsn(sentryDsn);
            options.setTag("user", "SCOUTER");
            options.setTag("environment", "SCOUTER_ENV");
            Sentry.captureException(exception);
        });
        Logger.println("Success Send Message");
        Logger.println("SentryDsn: " + sentryDsn);
        Logger.println("Message : " + exception.getMessage());
    }
}
