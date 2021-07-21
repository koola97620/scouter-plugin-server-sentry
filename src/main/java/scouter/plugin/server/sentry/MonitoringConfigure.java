package scouter.plugin.server.sentry;

import scouter.server.Configure;

public class MonitoringConfigure {
    private Configure configure;

    public MonitoringConfigure(Configure configure) {
        this.configure = configure;
    }


    public String getValue(String key, String objType, String defaultValue) {
        return defaultValue;
    }
}
