package com.jsdroid.pro;

import android.util.Log;

import com.jsdroid.api.IJsDroidShell;
import com.jsdroid.runner.JsDroidApplication;

public class App extends JsDroidApplication {
    public static App getInstance() {
        return JsDroidApplication.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("JsDroid", "version: " + versionName());
    }

    @Override
    public void onScriptPrint(String text) {
        super.onScriptPrint(text);
        Log.d("JsDroid", "onScriptPrint: "+text);
    }

    @Override
    public void onLoadScript(String file) {
        super.onLoadScript(file);
    }

    @Override
    public void onJsDroidConnected() {
        super.onJsDroidConnected();
        try {
            IJsDroidShell jsDroidShell = getJsDroidShell();
            jsDroidShell.runCode("print 1");
        } catch (Exception e) {
        }
    }
}
