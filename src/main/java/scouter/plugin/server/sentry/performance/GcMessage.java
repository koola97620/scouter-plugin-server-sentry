package scouter.plugin.server.sentry.performance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GcMessage implements Message {
    private final String objName;
    private final String objType;
    private final long gcTime;
    private final long gcThreshold;
    private final LocalDateTime currentDateTime;

    public GcMessage(PerformanceInfo performanceInfo) {
        objName = performanceInfo.getObjectInfo().getObjName();
        objType = performanceInfo.getObjectInfo().getObjType();
        gcTime = performanceInfo.getGcTimeInfo().getGcTime();
        gcThreshold = performanceInfo.getGcTimeInfo().getGcThreshold();
        currentDateTime = LocalDateTime.now();
    }

    @Override
    public RuntimeException throwException() {
        return new GcTimeOverThreshold(createErrorMessage());
    }

    private String createErrorMessage() {
        return
                "[GC Time is over threshold]" + "\n" +
                        "[ObjName]=" + objName + "\n" +
                        "[ObjType]=" + objType + "\n" +
                        "[GcTime]=" + gcTime + "\n" +
                        "[GcThreshold]=" + gcThreshold + "\n" +
                        "[Title]= GC Time exceed a threshold." + "\n" +
                        "[Content]= " + objName + "'s GC Time (" + gcTime + "ms) exceed a threshold." + "\n" +
                        "[CurrentTime]=" + currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
