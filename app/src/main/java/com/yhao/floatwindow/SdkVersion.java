package com.yhao.floatwindow;

import android.os.Build;

public class SdkVersion {
    public static final int SDK_INT = Build.VERSION.SDK_INT
            + ("REL".equals(Build.VERSION.CODENAME) ? 0 : 1);
}
