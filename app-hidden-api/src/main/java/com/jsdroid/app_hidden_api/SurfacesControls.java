package com.jsdroid.app_hidden_api;

import android.os.IBinder;
import android.view.Surface;
import android.view.SurfaceControl;

public class SurfacesControls {
    public static IBinder createDisplay(String name, boolean secure) {
        return SurfaceControl.createDisplay(name, secure);
    }

    public static void openTransaction() {
        SurfaceControl.openTransaction();
    }

    public static void setDisplaySurface(IBinder displayToken, Surface surface) {
        SurfaceControl.setDisplaySurface(displayToken, surface);
    }

    public static void setDisplayLayerStack(IBinder displayToken, int layerStack) {
        SurfaceControl.setDisplayLayerStack(displayToken, layerStack);
    }

    public static void closeTransaction() {
        SurfaceControl.closeTransaction();
    }

}
