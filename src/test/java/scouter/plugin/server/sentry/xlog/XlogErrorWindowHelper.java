package scouter.plugin.server.sentry.xlog;

public class XlogErrorWindowHelper {
    private XlogErrorWindow window;

    public XlogErrorWindowHelper(XlogErrorWindow window) {
        this.window = window;
    }

    public void registFailure(XlogData xlog,int errorTimeRange) {
        window.registFailure(xlog,errorTimeRange);
    }

    public void registFailure(XlogData xlog) {
        window.regist(xlog);
    }

    public int size() {
        return window.size();
    }

    public boolean isFailure(int countThreshold) {
        return window.isFailure(countThreshold);
    }
}
