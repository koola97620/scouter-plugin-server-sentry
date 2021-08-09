package scouter.plugin.server.sentry.performance;

import scouter.lang.counters.CounterConstants;
import scouter.lang.pack.PerfCounterPack;

public class TpsInfo {
    private final float tpsThreshold;
    private final float tps;

    public TpsInfo(MonitoringGroupConfigure groupConf, PerfCounterPack pack, ObjectInfo objectInfo) {
        tpsThreshold = groupConf.getFloat(ConfigurationConstants.TPS_THRESHOLD, objectInfo.getObjType(), 0);
        tps = pack.data.getFloat(CounterConstants.WAS_TPS);
    }

    public TpsInfo(float tpsThreshold, float tps) {
        this.tpsThreshold = tpsThreshold;
        this.tps = tps;
    }

    public boolean isOverThreshold() {
        return tpsThreshold != 0 && tps > tpsThreshold;
    }

    public float getTpsThreshold() {
        return tpsThreshold;
    }

    public float getTps() {
        return tps;
    }
}
