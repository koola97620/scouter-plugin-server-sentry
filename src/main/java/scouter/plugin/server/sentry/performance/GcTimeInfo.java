package scouter.plugin.server.sentry.performance;

import scouter.lang.counters.CounterConstants;
import scouter.lang.pack.PerfCounterPack;

public class GcTimeInfo {
    private final long gcThreshold;
    private final long gcTime;

    public GcTimeInfo(MonitoringGroupConfigure groupConf, PerfCounterPack pack, ObjectInfo objectInfo) {
        gcThreshold = groupConf.getLong(ConfigurationConstants.GC_TIME_THRESHOLD, objectInfo.getObjType(), 0);
        gcTime = pack.data.getLong(CounterConstants.JAVA_GC_TIME);
    }

    public GcTimeInfo(long gcThreshold, long gcTime) {
        this.gcThreshold = gcThreshold;
        this.gcTime = gcTime;
    }

    public boolean isOverThreshold() {
        return gcThreshold != 0 && gcTime > gcThreshold;
    }

    public long getGcThreshold() {
        return gcThreshold;
    }

    public long getGcTime() {
        return gcTime;
    }
}
