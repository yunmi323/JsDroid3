package com.jsdroid.server;

import android.util.Log;

import com.jsdroid.api.IJsDroidServer;
import com.jsdroid.api.IJsDroidService;
import com.jsdroid.box.BoxServer;
import com.jsdroid.ipc.Ipc;
import com.jsdroid.sdk.inputs.Inputs;
import com.jsdroid.sdk.system.Loop;

public class JsDroidServer implements IJsDroidServer {
    @Override
    public void onServerStart(final Ipc.Server server) {
        Log.d("JsDroid", "server start.");
        server.addService(IJsDroidService.SERVICE_NAME, JsDroidService.getInstance());
        server.execute(new Runnable() {
            @Override
            public void run() {
                Loop.loop(new Runnable() {
                    @Override
                    public void run() {
                        Inputs.getInstance().closeInputMethod();
                    }
                });
            }
        });
        try {
            Class.forName("com.jsdroid.box.BoxServer").newInstance();
        } catch (Exception e) {
        }
    }

}
