package scouter.plugin.server.sentry.xlog;

public class ErrorXlog implements XlogData {
    private long time;
    private String objName;

    public ErrorXlog(long time, String objName) {
        this.time = time;
        this.objName = objName;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public String getObjName() {
        return this.objName;
    }

}
