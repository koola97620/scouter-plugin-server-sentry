package scouter.plugin.server.sentry.performance;

import scouter.lang.counters.CounterConstants;
import scouter.lang.pack.PerfCounterPack;

public class ActiveServiceInfo {

    private final int activeServiceThreshold;
    private final int activeService;

    public ActiveServiceInfo(MonitoringGroupConfigure groupConf, PerfCounterPack pack, ObjectInfo objectInfo) {
        activeServiceThreshold = groupConf.getInt(ConfigurationConstants.ACTIVE_SERVICE_THRESHOLD, objectInfo.getObjType(), 0);
        activeService = pack.data.getInt(CounterConstants.WAS_ACTIVE_SERVICE);
    }

    public ActiveServiceInfo(int activeServiceThreshold, int activeService) {
        this.activeServiceThreshold = activeServiceThreshold;
        this.activeService = activeService;

    }

    public int getActiveServiceThreshold() {
        return activeServiceThreshold;
    }

    public int getActiveService() {
        return activeService;
    }

    public boolean isOverThreshold() {
        return activeServiceThreshold != 0 && activeService > activeServiceThreshold;
    }
}
