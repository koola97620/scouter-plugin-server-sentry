package scouter.plugin.server.sentry;

import scouter.lang.pack.PerfCounterPack;
import scouter.lang.plugin.PluginConstants;
import scouter.lang.plugin.annotation.ServerPlugin;
import scouter.plugin.server.sentry.performance.ConfigurationConstants;
import scouter.plugin.server.sentry.performance.MonitoringGroupConfigure;
import scouter.plugin.server.sentry.performance.PerformanceService;
import scouter.plugin.server.sentry.send.MessageSender;
import scouter.plugin.server.sentry.send.SentrySender;
import scouter.server.Configure;
import scouter.server.Logger;

/**
 * @author JDragon
 */
public class SentryPlugin {
    Configure conf = Configure.getInstance();

    private MonitoringGroupConfigure groupConf;
    private final MessageSender sender;

    public SentryPlugin() {
        groupConf = new MonitoringGroupConfigure(conf);

        String sentryDsn = groupConf.getSentryDsn(ConfigurationConstants.SENTRY_DSN);

        sender = new SentrySender(sentryDsn);
    }

//    @ServerPlugin(PluginConstants.PLUGIN_SERVER_ALERT)
//    public void alert(AlertPack pack) {
//        if (conf.getBoolean("ext_plugin_sentry_alert_enabled", false)) {
//            println("[SentryPlugin-alert] " + pack);
//        }
//    }

    // 스카우터 서버에서 Performance Counter 정보가 수집될 때마다 해당 메서드가 호출된다.
    // PerfCounterPack 클래스는 수집된 counter 정보가 담겨있다.
    @ServerPlugin(PluginConstants.PLUGIN_SERVER_COUNTER)
    public void counter(PerfCounterPack pack) {
        if (!isPossibleCollectPerformanceCounter()) {
            return;
        }

        try {
            PerformanceService performanceService = new PerformanceService(conf, groupConf, sender);
            performanceService.process(pack);
        } catch (Exception e) {
            println("[SentryPlugin-Counter] " + pack);
            printStackTrace(e);
        }
    }

//    @ServerPlugin(PluginConstants.PLUGIN_SERVER_OBJECT)
//    public void object(ObjectPack pack) {
//        if (conf.getBoolean("ext_plugin_sentry_object_enabled", false)) {
//            println("[SentryPlugin-object] " + pack);
//        }
//    }

//    @ServerPlugin(PluginConstants.PLUGIN_SERVER_SUMMARY)
//    public void summary(SummaryPack pack) {
//        if (conf.getBoolean("ext_plugin_sentry_summary_enabled", false)) {
//            println("[SentryPlugin-summary] " + pack);
//        }
//    }

//    @ServerPlugin(PluginConstants.PLUGIN_SERVER_XLOG)
//    public void xlog(XLogPack pack) {
//        if (isPossibleCollectXlog()) {
//            return;
//        }
//
//    }

    //    @ServerPlugin(PluginConstants.PLUGIN_SERVER_PROFILE)
//    public void profile(XLogProfilePack pack) {
//        if (conf.getBoolean("ext_plugin_sentry_profile_enabled", false)) {
//            println("[SentryPlugin-profile] " + pack);
//        }
//    }
    private boolean isPossibleCollectPerformanceCounter() {
        return conf.getBoolean(ConfigurationConstants.COUNTER_ENABLED, false);
    }

    private boolean isPossibleCollectXlog() {
        return conf.getBoolean(ConfigurationConstants.XLOG_ENABLED, false);
    }

    private boolean isServerLogEnabled() {
        return conf.getBoolean(ConfigurationConstants.SERVER_LOG_ENABLED, false);
    }

    private void println(Object o) {
        if (isServerLogEnabled()) {
            Logger.println(o);
        }
    }

    private void printStackTrace(Exception o) {
        if (isServerLogEnabled()) {
            Logger.printStackTrace(o);
        }
    }

}
