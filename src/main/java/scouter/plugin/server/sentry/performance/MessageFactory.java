package scouter.plugin.server.sentry.performance;

import java.util.Optional;

public class MessageFactory {
    public static Optional<Message> createMessage(PerformanceInfo performanceInfo) {
        if (!performanceInfo.isOccurfailure()) {
            return Optional.empty();
        }

        if (performanceInfo.isGcTimeOverThreshold()) {
            return Optional.of(new GcMessage(performanceInfo));
        }

        if (performanceInfo.isTpsOverThreshold()) {
            return Optional.of(new TpsMessage(performanceInfo));
        }

        if (performanceInfo.isActiveServiceOverThreshold()) {
            return Optional.of(new ActiveServiceMessage(performanceInfo));
        }

        return Optional.of(new MultiMessage(performanceInfo));
    }
}
