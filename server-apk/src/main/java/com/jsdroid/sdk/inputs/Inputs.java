package com.jsdroid.sdk.inputs;

import android.util.Log;

import com.jsdroid.app_hidden_api.InputUtil;
import com.jsdroid.sdk.nodes.UiAutomationService;

import java.util.Stack;

public class Inputs {
    private static class Single {
        static Inputs single = new Inputs();
    }

    public static Inputs getInstance() {

        return Single.single;
    }

    private Stack<String> stack = new Stack<>();

    private Inputs() {

    }

    public void openInputMethod() {
        try {
            String ime_id = System.getenv("ime_id");
            if (ime_id != null) {
                InputUtil.setInputMethod(ime_id);
            }
        } catch (Throwable e) {
            Log.e("JsDroid", "openInputMethod: ", e);
        }
    }


    public void closeInputMethod() {
        try {
            openInputMethod();
            InputUtil.switchToLastInputMethod();
        } catch (Throwable e) {
            Log.e("JsDroid", "openInputMethod: ", e);
        }
    }


    public synchronized void onScriptStart(String pkg) {
        if (stack.isEmpty()) {
            openInputMethod();
        }
        stack.push(pkg);

    }

    public synchronized void onScriptStop(String pkg) {
        try {
            stack.pop();
        } catch (Exception e) {
        }
        if (stack.isEmpty()) {
            closeInputMethod();
        }
    }


}
