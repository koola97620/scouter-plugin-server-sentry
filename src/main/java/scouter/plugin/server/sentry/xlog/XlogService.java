package scouter.plugin.server.sentry.xlog;

import scouter.lang.pack.XLogPack;
import scouter.plugin.server.sentry.performance.ConfigurationConstants;
import scouter.plugin.server.sentry.performance.MonitoringGroupConfigure;
import scouter.plugin.server.sentry.send.MessageSender;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class XlogService {
    private static final Integer MESSAGE_SEND_WAITING_TIME = 30;
    private final MonitoringGroupConfigure groupConf;
    private final MessageSender sender;

    public XlogService(MonitoringGroupConfigure groupConf, MessageSender sender) {
        this.groupConf = groupConf;
        this.sender = sender;
    }

    private XlogErrorWindow xlogErrorWindow = new XlogErrorWindow();
    private LocalDateTime restartCountTime = null;
    private ReentrantLock lock = new ReentrantLock();

    public void process(XLogPack pack, String objType, String objName) throws InterruptedException {
        if (isSentryAlertEnabled(objType)) {
            if (pack.error != 0) {
                if (restartCountTime != null && restartCountTime.isAfter(new Timestamp(pack.endTime).toLocalDateTime())) {
                    return;
                }
                int errorCountTimeRange = groupConf.getInt(ConfigurationConstants.ERROR_TIME_RANGE, null, 0);
                int errorCountThreshold = groupConf.getInt(ConfigurationConstants.ERROR_COUNT, null, 0);
                if (lock.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        xlogErrorWindow.registFailure(
                                new ErrorXlog(pack.endTime, objName),
                                errorCountTimeRange);
                        if (xlogErrorWindow.size() >= errorCountThreshold) {
                            String message = createErrorMessage(xlogErrorWindow.getXlogDataQueue());
                            sender.send(new ErrorCountOverThresholdInTime(message));
                            xlogErrorWindow.initQueue();
                            restartCountTime = new Timestamp(pack.endTime).toLocalDateTime().plusSeconds(MESSAGE_SEND_WAITING_TIME);
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
    }

    public XlogErrorWindow getXlogErrorWindow() {
        return xlogErrorWindow;
    }

    private boolean isSentryAlertEnabled(String objType) {
        return groupConf.getBoolean(ConfigurationConstants.ALERT_ENABLED, objType, false);
    }

    private String createErrorMessage(ConcurrentLinkedQueue<XlogData> xlogDataQueue) {
        return xlogDataQueue.stream()
                .collect(Collectors.groupingBy(XlogData::getObjName, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(",", "[", "]"));
    }

}
