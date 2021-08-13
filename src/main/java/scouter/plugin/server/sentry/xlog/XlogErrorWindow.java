package scouter.plugin.server.sentry.xlog;

import java.util.concurrent.ConcurrentLinkedQueue;

public class XlogErrorWindow {
    private ConcurrentLinkedQueue<XlogData> xlogDataQueue;

    public XlogErrorWindow() {
        xlogDataQueue = new ConcurrentLinkedQueue<>();
    }

    public void regist(XlogData xlogData) {
        xlogDataQueue.add(xlogData);
    }

    public boolean isFailure(int countThreshold) {
        return xlogDataQueue.size() >= countThreshold;
    }

    public int size() {
        return xlogDataQueue.size();
    }

    public void initQueue() {
        xlogDataQueue = new ConcurrentLinkedQueue<>();
    }

    public ConcurrentLinkedQueue<XlogData> getXlogDataQueue() {
        return xlogDataQueue;
    }

    public void registFailure(XlogData errorXlog, int errorCountTimeRange) {
        xlogDataQueue.add(errorXlog);
        long deleteStandardTime = errorXlog.getTime() - 1000 * errorCountTimeRange;
        xlogDataQueue.removeIf(log -> log.getTime() < deleteStandardTime);
    }
}
