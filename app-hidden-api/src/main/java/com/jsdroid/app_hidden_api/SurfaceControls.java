package com.jsdroid.app_hidden_api;

import android.graphics.Rect;
import android.os.IBinder;
import android.view.Surface;
import android.view.SurfaceControl;

public class SurfaceControls {
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
    public static void destroySurface(IBinder display){
        SurfaceControl.destroyDisplay(display);
    }
    public static void setDisplayProjection(IBinder displayToken, int orientation, Rect layerStackRect, Rect displayRect) {
        SurfaceControl.setDisplayProjection(displayToken, orientation, layerStackRect, displayRect);
    }
}
