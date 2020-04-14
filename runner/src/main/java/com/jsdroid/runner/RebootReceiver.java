package com.jsdroid.runner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (Config.PRO) {
                launchActivity(context);
            }
        }
    }

    private void launchActivity(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
        context.startActivity(intent);
        if (JsDroidApplication.getInstance().isRebootRun()) {
            JsDroidApplication.getInstance().waitServerAndStartScript();
        }
    }

}
