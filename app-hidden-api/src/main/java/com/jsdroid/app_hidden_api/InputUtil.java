package com.jsdroid.app_hidden_api;


import android.content.Context;
import android.hardware.input.IInputManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemService;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.android.internal.view.IInputMethodManager;

import java.util.ArrayList;
import java.util.List;

public class InputUtil {
    static IInputMethodManager im;

    public static void setInputMethod(String id) {
        try {
            try {
                if (im == null) {
                    im = IInputMethodManager.Stub.asInterface(ServiceManager.getService(Context.INPUT_METHOD_SERVICE));
                }
                im.setInputMethodEnabled(id, true);
            } catch (Throwable ignore) {
            }
            InputMethodManager.getInstance().setInputMethod(null, id);
        } catch (Throwable e) {
        }

    }

    public static List<String> list() {
        List<String> ret = new ArrayList<>();
        try {
            for (InputMethodInfo inputMethodInfo : InputMethodManager.getInstance().getInputMethodList()) {
                ret.add(inputMethodInfo.getId());
            }
        } catch (Throwable e) {
        }
        return ret;
    }

    public static void switchToLastInputMethod() {
        try {
            InputMethodManager.getInstance().switchToLastInputMethod(null);
        } catch (Throwable e) {
        }
    }
}
