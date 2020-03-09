package com.jsdroid.app_hidden_api;


import android.content.Context;
import android.hardware.input.IInputManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemService;
import android.view.inputmethod.InputMethodManager;

import com.android.internal.view.IInputMethodManager;

public class InputUtil {
    static IInputMethodManager im;
    public static void setInputMethod(String id) {
        try {
            try {
                if (im==null) {
                    im = IInputMethodManager.Stub.asInterface(ServiceManager.getService(Context.INPUT_METHOD_SERVICE));
                }
                im.setInputMethodEnabled(id,true);
            } catch (Throwable ignore) {
            }
            InputMethodManager.getInstance().setInputMethod(null, id);
        } catch (Exception e) {
        }

    }

    public static void switchToLastInputMethod() {
        try {
            InputMethodManager.getInstance().switchToLastInputMethod(null);
        } catch (Exception e) {
        }
    }
}
