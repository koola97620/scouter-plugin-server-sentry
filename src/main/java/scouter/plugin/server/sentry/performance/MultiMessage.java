package scouter.plugin.server.sentry.performance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MultiMessage implements Message {

    private final String objName;
    private final String objType;
    private final long gcTime;
    private final long gcThreshold;
    private final float tps;
    private final float tpsThreshold;
    private final LocalDateTime currentDateTime;
    private final int activeService;
    private final int activeServiceThreshold;

    public MultiMessage(PerformanceInfo performanceInfo) {
        objName = performanceInfo.getObjectInfo().getObjName();
        objType = performanceInfo.getObjectInfo().getObjType();
        gcTime = performanceInfo.getGcTimeInfo().getGcTime();
        gcThreshold = performanceInfo.getGcTimeInfo().getGcThreshold();
        activeService = performanceInfo.getActiveServiceInfo().getActiveService();
        activeServiceThreshold = performanceInfo.getActiveServiceInfo().getActiveServiceThreshold();
        tps = performanceInfo.getTpsInfo().getTps();
        tpsThreshold = performanceInfo.getTpsInfo().getTpsThreshold();
        currentDateTime = LocalDateTime.now();
    }

    @Override
    public RuntimeException throwException() {
        return new MultipleOverThreshold(createErrorMessage());
    }

    private String createErrorMessage() {
        return
                "[Various failure is occured]" + "\n" +
                        "[ObjName]= " + objName + "\n" +
                        "[ObjType]= " + objType + "\n" +
                        "[GcTime]= " + gcTime + "\n" +
                        "[GcThreshold]= " + gcThreshold + "\n" +
                        "[ActiveService]= " + activeService + "\n" +
                        "[ActiveServiceThreshold]= " + activeServiceThreshold + "\n" +
                        "[Tps]= " + tps + "\n" +
                        "[TpsThreshold]= " + tpsThreshold + "\n" +
                        "[Title] = Various performance exceed threshold." + "\n" +
                        "[CurrentTime]= " + currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
