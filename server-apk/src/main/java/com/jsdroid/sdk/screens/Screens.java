package com.jsdroid.sdk.screens;

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;

import com.jsdroid.app_hidden_api.SurfacesControls;
import com.jsdroid.sdk.devices.Devices;

import java.util.ArrayList;
import java.util.List;

public class Screens {
    private static class Single {
        static Screens instance = new Screens();
    }

    public static Screens getInstance() {
        return Single.instance;
    }

    public interface FrameListener {
        void onFrameUpdate(Screens screens);
    }

    private int screenWidth;
    private int screenHeight;
    private IBinder display;
    private ImageReader imageReader;
    private Bitmap cache;
    private int cacheWidth;
    private int cacheHeight;
    private HandlerThread captureThread;
    private Devices devices;
    private final List<FrameListener> frameListeners;

    private Screens() {
        frameListeners = new ArrayList<>();
        devices = Devices.getInstance();
        captureThread = new HandlerThread("screen");
        captureThread.start();
        display = SurfacesControls.createDisplay("screen", false);

    }


    public void addListener(FrameListener frameListener) {
        frameListeners.add(frameListener);
    }


    public Bitmap capture() throws InterruptedException {
        return capture(1);
    }


    public Bitmap capture(float scale) throws InterruptedException {
        checkImageReader();
        readImage();
        synchronized (Screens.this) {
            if (cache == null) {
                Screens.this.wait(1000);
                readImage();
            }
            if (cache != null) {
                return scaleBitmap(cache, cacheWidth, cacheHeight, scale);
            }
            return null;
        }
    }

    private void readImage() {
        try {
            Image image = imageReader.acquireLatestImage();
            if (image != null) {
                try {
                    synchronized (Screens.this) {
                        if (cache != null) {
                            cache.recycle();
                        }
                        cacheWidth = image.getWidth();
                        cacheHeight = image.getHeight();
                        cache = readBitmap(image);
                        Screens.this.notifyAll();
                    }
                } finally {
                    image.close();
                }
            }else{
                Log.d("JsDroid", "readImage: image is null",new Exception());
            }
        } catch (Throwable e) {
            Log.d("JsDroid", "readImage: ",e);
        }
    }

    private Bitmap readBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        Image.Plane plane = planes[0];
        int stride = plane.getPixelStride();
        int rowStride = plane.getRowStride();
        int rowPadding = rowStride - stride * image.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(
                image.getWidth() + rowPadding / stride,
                image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(plane.getBuffer());
        return bitmap;
    }

    private void getScreenSize() {
        int rotation = devices.getRotation();
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            screenWidth = devices.getNaturalWidth();
            screenHeight = devices.getNaturalHeight();
        } else {
            screenHeight = devices.getNaturalWidth();
            screenWidth = devices.getNaturalHeight();
        }
    }

    private synchronized void fireFrameUpdate() {
        synchronized (Screens.class) {
            Screens.class.notifyAll();
        }
        try {
            for (FrameListener frameListener : frameListeners) {
                frameListener.onFrameUpdate(Screens.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void checkImageReader() {
        try {
            getScreenSize();
            if (imageReader == null) {
                imageReader = createImageReader();
            } else {
                if (screenWidth != imageReader.getWidth() || screenHeight != imageReader.getHeight()) {
                    try {
                        imageReader.close();
                    } catch (Throwable e) {
                    }
                    imageReader = createImageReader();
                }
            }
        } catch (Exception e) {
            Log.d("JsDroid", "checkImageReader: ",e);
        }

    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }


    public void removeListener(FrameListener frameListener) {
        frameListeners.remove(frameListener);
    }

    private ImageReader createImageReader() {
        ImageReader imageReader = ImageReader.newInstance(screenWidth, screenHeight,0x1, 1);
        SurfacesControls.openTransaction();
        SurfacesControls.setDisplaySurface(display, imageReader.getSurface());
        SurfacesControls.setDisplayLayerStack(display, 0);
        SurfacesControls.closeTransaction();
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imageReader) {
                try {
                    fireFrameUpdate();
                    checkImageReader();
                } catch (Throwable e) {
                    Log.d("JsDroid", "onImageAvailable: ",e);
                }
            }
        }, new Handler(captureThread.getLooper()));
        return imageReader;
    }


    private Bitmap scaleBitmap(Bitmap bitmap, int originWidth, int originHeight, float scale) {
        if (originWidth == 0 || originHeight == 0) {
            return null;
        }
        if (scale == 1) {
            return Bitmap.createBitmap(bitmap, 0, 0, originWidth, originHeight);
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, originWidth, originHeight, matrix, false);
    }
}
