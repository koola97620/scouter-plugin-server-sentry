package scouter.plugin.server.sentry.xlog;

public class XlogStub implements XlogData {
    private long time;
    private String objName;

    public XlogStub(long time) {
        this.time = time;
    }

    public XlogStub(long time, String objName) {
        this.time = time;
        this.objName = objName;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public String getObjName() {
        return this.objName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        XlogStub xlogStub = (XlogStub) o;

        return time == xlogStub.time;
    }

    @Override
    public int hashCode() {
        return (int) (time ^ (time >>> 32));
    }
}
