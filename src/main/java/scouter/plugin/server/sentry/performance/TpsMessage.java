package scouter.plugin.server.sentry.performance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TpsMessage implements Message {
    private final String objName;
    private final String objType;
    private final float tps;
    private final float tpsThreshold;
    private final LocalDateTime currentDateTime;

    public TpsMessage(PerformanceInfo performanceInfo) {
        objName = performanceInfo.getObjectInfo().getObjName();
        objType = performanceInfo.getObjectInfo().getObjType();
        tps = performanceInfo.getTpsInfo().getTps();
        tpsThreshold = performanceInfo.getTpsInfo().getTpsThreshold();
        currentDateTime = LocalDateTime.now();
    }

    @Override
    public RuntimeException throwException() {
        return new TpsOverThreshold(createErrorMessage());
    }

    private String createErrorMessage() {
        return
                "[Tps is over threshold]" + "\n" +
                        "[ObjName]= " + objName + "\n" +
                        "[ObjType]= " + objType + "\n" +
                        "[Tps]= " + tps + "\n" +
                        "[TpsThreshold]= " + tpsThreshold + "\n" +
                        "[Title]= Tps exceed a threshold." + "\n" +
                        "[Content]= " + objName + "'s TPS (" + tps + ") exceed a threshold." + "\n" +
                        "[CurrentTime]= " + currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
