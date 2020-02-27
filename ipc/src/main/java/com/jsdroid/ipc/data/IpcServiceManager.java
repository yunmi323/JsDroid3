package com.jsdroid.ipc.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

//该类存了大量的Service：虚拟接口，每个接口的实例都会有个缓存，每实例化一个接口，都会增加系统内存，Proxy断开后，由系统回收这些内存
public class IpcServiceManager {
    //自动回收表
    private WeakHashMap<String, Object> serviceWeakMap;
    private Map<String, Object> serviceMap;

    public IpcServiceManager() {
        serviceWeakMap = new WeakHashMap<>();
        serviceMap = new HashMap<>();
    }

    public Object getService(String id) {
        if (serviceMap.containsKey(id)) {
            return serviceMap.get(id);
        }
        return serviceWeakMap.get(id);
    }

    public String addService(String id, Object service) {
        if (service instanceof IpcService) {
            if (!((IpcService) service).needGc()) {
                serviceMap.put(id, service);
            }
        }
        serviceWeakMap.put(id, service);
        return id;
    }


    public void removeService(String id) {
        serviceMap.remove(id);
        serviceWeakMap.remove(id);
    }
}
