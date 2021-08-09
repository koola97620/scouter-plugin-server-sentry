package scouter.plugin.server.sentry.performance;

import scouter.server.Configure;

public class MonitoringGroupConfigure {
    private Configure conf;

    public MonitoringGroupConfigure(Configure conf) {
        this.conf = conf;
    }

    public String getValue(String key, String objType) {
        return this.getValue(key, objType, null);
    }

    public String getGroupKey(String originalKey, String objType) {
        if (originalKey == null) {
            return originalKey;
        }
        return objType + "." + originalKey;
    }

    public String getValue(String key, String objType, String defaultValue) {
        String groupKey = getGroupKey(key, objType);
        String value = conf.getValue(groupKey);
        System.out.println("groupkey: " + groupKey);
        System.out.println("value: " + value);
        if (value != null && value.trim().length() > 0) {
            return value;
        }
        value = conf.getValue(key);
        return value != null ? null : defaultValue;
    }

    private Boolean toBoolean(String value) {
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return null;
    }

    public Long getLong(String key, String objType, long defaultValue) {
        String groupKey = getGroupKey(key, objType);
        Long value = toLong(conf.getValue(groupKey));
        if (value != null) {
            return value;
        }
        value = toLong(conf.getValue(key));
        return value != null ? value : defaultValue;

    }

    private Long toLong(String value) {
        if (value != null) {
            return Long.parseLong(value);
        }
        return null;
    }

    public boolean getBoolean(String key, String objType, boolean defaultValue) {
        String groupKey = getGroupKey(key, objType);
        Boolean value = toBoolean(conf.getValue(groupKey));
        if (value != null) {
            return value;
        }
        value = toBoolean(conf.getValue(key));
        return value != null ? value : defaultValue;
    }

    public float getFloat(String key, String objType, float defaultValue) {
        String groupKey = getGroupKey(key, objType);
        Float value = toFloat(conf.getValue(groupKey));
        if (value != null) {
            return value;
        }
        value = toFloat(conf.getValue(key));
        return value != null ? value : defaultValue;
    }

    private Float toFloat(String value) {
        if (value != null) {
            return Float.parseFloat(value);
        }
        return null;
    }

    public int getInt(String key, String objType, int defaultValue) {
        String groupKey = getGroupKey(key, objType);
        Integer value = toInteger(conf.getValue(groupKey));
        if (value != null) {
            return value;
        }
        value = toInteger(conf.getValue(key));
        return value != null ? value : defaultValue;
    }

    private Integer toInteger(String value) {
        if (value != null) {
            return Integer.parseInt(value);
        }
        return null;
    }

    public String getSentryDsn(String key) {
        return conf.getValue(key) == null ? "" : conf.getValue(key);
    }
}
