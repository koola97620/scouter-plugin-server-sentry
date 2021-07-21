package scouter.plugin.server.sentry;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class SentryTest {
    @Test
    void sentryTest() {
        try {
            throw new RuntimeException();
        } catch (Exception e) {
            Sentry.init("https://822a2e9a75d449eca6332d2cee3c58e0@o372444.ingest.sentry.io/5187157");

            Sentry.withScope(scope -> {
                scope.setLevel(SentryLevel.ERROR);
                scope.setTag("scouter", "SCOUTER");
                scope.setTag("environment", "SCOUTER_ENV");
                scope.setTag("slack-channel", "DEV-ERR");
                Sentry.captureException(e);
            });
        }
    }
}
