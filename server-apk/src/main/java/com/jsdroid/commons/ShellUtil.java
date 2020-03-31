package com.jsdroid.commons;

import com.jsdroid.sdk.shells.Shells;

public class ShellUtil {
    public static String exec(String shell) {
        return Shells.getInstance().exec(shell);
    }
}
