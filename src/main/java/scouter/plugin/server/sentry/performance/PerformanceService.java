package scouter.plugin.server.sentry.performance;

import scouter.lang.TimeTypeEnum;
import scouter.lang.counters.CounterConstants;
import scouter.lang.pack.PerfCounterPack;
import scouter.plugin.server.sentry.send.MessageSender;
import scouter.server.Configure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PerformanceService {
    private final Configure conf;
    private final MonitoringGroupConfigure groupConf;
    private final MessageSender sender;
    private static List<Integer> javaeeObjHashList = new ArrayList<Integer>();

    public PerformanceService(Configure conf, MonitoringGroupConfigure groupConf, MessageSender sender) {
        this.conf = conf;
        this.groupConf = groupConf;
        this.sender = sender;
    }

    public void process(PerfCounterPack pack) {
        ObjectInfo objectInfo = new ObjectInfo(pack);
        if (isSentryAlertEnabled(objectInfo)) {
            if (CounterConstants.FAMILY_JAVAEE.equals(objectInfo.getObjFamily())) {
                if (!javaeeObjHashList.contains(objectInfo.getObjHash())) {
                    javaeeObjHashList.add(objectInfo.getObjHash());
                }

                if (pack.timetype == TimeTypeEnum.REALTIME) {
                    PerformanceCollector performanceCollector = new PerformanceCollector(groupConf,pack,objectInfo);
                    PerformanceInfo performanceInfo = performanceCollector.collect();

                    Optional<Message> message = MessageFactory.createMessage(performanceInfo);
                    message.ifPresent(msg -> sender.send(msg.throwException()));
                }
            }
        }
    }


    private boolean isSentryAlertEnabled(ObjectInfo objectInfo) {
        return groupConf.getBoolean(ConfigurationConstants.ALERT_ENABLED, objectInfo.getObjType(), false);
    }

}
