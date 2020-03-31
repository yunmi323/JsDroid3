package com.jsdroid.test.widget;

import android.util.Log;
import android.webkit.ConsoleMessage;

import com.jsdroid.runner.JsDroidApplication;
import com.jsdroid.test.JsdApp;
import com.just.agentweb.WebChromeClient;

public class OptionChromeClient extends WebChromeClient {
    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        onConsoleMessage(consoleMessage.message(), consoleMessage.lineNumber(), consoleMessage.sourceId());
        return true;
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (sourceID != null) {
            if (sourceID.startsWith("option://jsdroid.com/")) {
                sourceID = sourceID.substring("option://jsdroid.com/".length());
            }
        } else {
            sourceID = "";
        }
        JsdApp.getInstance(JsdApp.class).print("@" + sourceID + "#" + lineNumber + "\n" + message);
    }
}
