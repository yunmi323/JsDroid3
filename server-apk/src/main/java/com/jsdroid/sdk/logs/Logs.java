package com.jsdroid.sdk.logs;

import android.util.Log;

import java.io.File;

public class Logs {

    private static class Single {
        public static Logs https = new Logs();
    }

    public static Logs getInstance() {
        return Single.https;
    }

    private Logs() {

    }

    public void d(String content) {
        Log.d("JsDroid", content);
    }

    public void d(String content, Throwable err) {
        Log.d("JsDroid", content, err);
    }

    public void e(String content) {
        Log.e("JsDroid", content);
    }

    public void e(String content, Throwable err) {
        Log.e("JsDroid", content, err);
    }


}
