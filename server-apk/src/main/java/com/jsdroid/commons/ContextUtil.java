package com.jsdroid.commons;

import android.app.ActivityThread;
import android.content.Context;

public class ContextUtil {

    public static Context getContext() {
        try {
            return ActivityThread.currentActivityThread().getApplication();
        } catch (Throwable e) {
        }
        return null;
    }

}
