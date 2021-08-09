package scouter.plugin.server.sentry.performance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActiveServiceMessage implements Message {
    private final String objName;
    private final String objType;
    private final long activeServiceCount;
    private final long activeServiceCountThreshold;
    private final LocalDateTime currentDateTime;

    public ActiveServiceMessage(PerformanceInfo performanceInfo) {
        objName = performanceInfo.getObjectInfo().getObjName();
        objType = performanceInfo.getObjectInfo().getObjType();
        activeServiceCount = performanceInfo.getActiveServiceInfo().getActiveService();
        activeServiceCountThreshold = performanceInfo.getActiveServiceInfo().getActiveServiceThreshold();
        currentDateTime = LocalDateTime.now();
    }

    @Override
    public RuntimeException throwException() {
        return new ActiveServiceOverThreshold(createErrorMessage());
    }

    private String createErrorMessage() {
        return
                "[ActiveService is over threshold]" + "\n" +
                        "[ObjName]=" + objName + "\n" +
                        "[ObjType]=" + objType + "\n" +
                        "[activeServiceCount]=" + activeServiceCount + "\n" +
                        "[activeServiceCountThreshold]=" + activeServiceCountThreshold + "\n" +
                        "[Title]= ActiveServiceCount exceed a threshold." + "\n" +
                        "[Content]= " + objName + "'s ActiveServiceCount (" + activeServiceCount + ") exceed a threshold." + "\n" +
                        "[CurrentTime]=" + currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
