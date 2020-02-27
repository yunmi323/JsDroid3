package com.jsdroid.sdk.screens;

import com.jsdroid.sdk.devices.Devices;

public class ScreenUtil {
    public static float getScale(int size) {
        if (size == 0) {
            return 0.1f;
        }
        Devices instance = Devices.getInstance();
        int width = instance.getWidth();
        int height = instance.getHeight();
        if (width > height) {
            return ((float) size) / width;
        } else {
            return ((float) size) / height;
        }
    }
}
