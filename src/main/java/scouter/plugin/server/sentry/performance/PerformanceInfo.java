package scouter.plugin.server.sentry.performance;

public class PerformanceInfo {
    private final ObjectInfo objectInfo;
    private final GcTimeInfo gcTimeInfo;
    private final TpsInfo tpsInfo;
    private final ActiveServiceInfo activeServiceInfo;

    private PerformanceInfo(builder builder) {
        objectInfo = builder.objectInfo;
        gcTimeInfo = builder.gcTimeInfo;
        tpsInfo = builder.tpsInfo;
        activeServiceInfo = builder.activeServiceInfo;
    }

    public boolean isGcTimeOverThreshold() {
        return gcTimeInfo.isOverThreshold() && !tpsInfo.isOverThreshold() && !activeServiceInfo.isOverThreshold();
    }

    public boolean isTpsOverThreshold() {
        return tpsInfo.isOverThreshold() && !gcTimeInfo.isOverThreshold() && !activeServiceInfo.isOverThreshold();
    }

    public boolean isActiveServiceOverThreshold() {
        return activeServiceInfo.isOverThreshold() && !gcTimeInfo.isOverThreshold() && !tpsInfo.isOverThreshold();
    }

    public boolean isOccurfailure() {
        return gcTimeInfo.isOverThreshold() || tpsInfo.isOverThreshold() || activeServiceInfo.isOverThreshold();
    }

    public static class builder {
        private ObjectInfo objectInfo;
        private GcTimeInfo gcTimeInfo;
        private TpsInfo tpsInfo;
        private ActiveServiceInfo activeServiceInfo;

        public builder() {

        }

        public builder objectInfo(ObjectInfo val) {
            objectInfo = val;
            return this;
        }

        public builder gcTimeInfo(GcTimeInfo val) {
            gcTimeInfo = val;
            return this;
        }

        public builder tpsInfo(TpsInfo val) {
            tpsInfo = val;
            return this;
        }

        public builder activeServiceInfo(ActiveServiceInfo val) {
            activeServiceInfo = val;
            return this;
        }

        public PerformanceInfo build() {
            return new PerformanceInfo(this);
        }

    }

    public ObjectInfo getObjectInfo() {
        return objectInfo;
    }

    public GcTimeInfo getGcTimeInfo() {
        return gcTimeInfo;
    }

    public TpsInfo getTpsInfo() {
        return tpsInfo;
    }

    public ActiveServiceInfo getActiveServiceInfo() {
        return activeServiceInfo;
    }
}
