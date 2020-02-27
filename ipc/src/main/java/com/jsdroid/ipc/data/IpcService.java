package com.jsdroid.ipc.data;

import com.jsdroid.ipc.call.ServiceProxy;

public interface IpcService {
    default void onAddService(String serviceId, ServiceProxy serviceProxy) {
    }

    default boolean needGc() {
        return true;
    }
}
