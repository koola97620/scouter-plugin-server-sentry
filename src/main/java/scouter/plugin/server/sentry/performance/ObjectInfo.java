package scouter.plugin.server.sentry.performance;

import scouter.lang.pack.PerfCounterPack;
import scouter.server.CounterManager;
import scouter.server.core.AgentManager;
import scouter.util.HashUtil;

public class ObjectInfo {
    private String objName;
    private int objHash;
    private String objType;
    private String objFamily;

    public ObjectInfo(PerfCounterPack pack) {
        objName = pack.objName;
        objHash = HashUtil.hash(objName);
        objType = null;
        objFamily = null;

        if (AgentManager.getAgent(objHash) != null) {
            objType = AgentManager.getAgent(objHash).objType;
        }

        if (objType != null) {
            objFamily = CounterManager.getInstance().getCounterEngine().getFamilyNameFromObjType(objType);
        }
    }

    public ObjectInfo(String objName, int objHash, String objType, String objFamily) {
        this.objName = objName;
        this.objHash = objHash;
        this.objType = objType;
        this.objFamily = objFamily;
    }

    public String getObjName() {
        return objName;
    }

    public int getObjHash() {
        return objHash;
    }

    public String getObjType() {
        return objType;
    }

    public String getObjFamily() {
        return objFamily;
    }


}
