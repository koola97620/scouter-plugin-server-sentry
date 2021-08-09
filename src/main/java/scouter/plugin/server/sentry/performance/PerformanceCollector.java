package scouter.plugin.server.sentry.performance;

import scouter.lang.pack.PerfCounterPack;

public class PerformanceCollector {
    private final MonitoringGroupConfigure groupConf;
    private final PerfCounterPack pack;
    private final ObjectInfo objectInfo;

    public PerformanceCollector(MonitoringGroupConfigure groupConf, PerfCounterPack pack, ObjectInfo objectInfo) {
        this.groupConf = groupConf;
        this.pack = pack;
        this.objectInfo = objectInfo;
    }

    public PerformanceInfo collect() {
        GcTimeInfo gcTimeInfo = new GcTimeInfo(groupConf, pack, objectInfo);
        TpsInfo tpsInfo = new TpsInfo(groupConf, pack, objectInfo);
        ActiveServiceInfo activeServiceInfo = new ActiveServiceInfo(groupConf, pack, objectInfo);
        return new PerformanceInfo.builder()
                .objectInfo(objectInfo)
                .gcTimeInfo(gcTimeInfo)
                .tpsInfo(tpsInfo)
                .activeServiceInfo(activeServiceInfo)
                .build();
    }
}
