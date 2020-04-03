package com.jsdroid.sdk.screens;

import android.app.UiAutomation;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.jsdroid.app_hidden_api.SurfaceControls;
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
    private final Object readLock = new Object();
    private Bitmap cache;
    private HandlerThread captureThread;
    private Devices devices;
    private final List<FrameListener> frameListeners;
    private boolean readOk;//格式是否正确

    private Screens() {
        frameListeners = new ArrayList<>();
        devices = Devices.getInstance();
        captureThread = new HandlerThread("screen");
        captureThread.start();
        display = SurfaceControls.createDisplay("screen", false);

    }


    public void addListener(FrameListener frameListener) {
        frameListeners.add(frameListener);
    }


    public Bitmap capture() throws InterruptedException {
        return capture(1);
    }

    private int imageFormat = -1;

    //首次截图需要判断格式是否正确
    private void getFrameFormatOnFirst() {
        // Image image = imageReader.acquireLatestImage();
        //The producer output buffer format 0x2 doesn't match the ImageReader's configured buffer format 0x1.
        if (readOk) {
            return;
        }
        try {
            Image image = imageReader.acquireLatestImage();
            readOk = true;
            if (image != null) {
                try {
                    cache = readBitmap(image);
                    cache = scaleBitmap(cache, screenWidth, screenHeight, 1.0f);
                } catch (Exception ignore) {
                } finally {
                    image.close();
                }
            }
        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null) {
                if (message.startsWith("The producer output buffer format")) {
                    String[] arr = message.split(" ");
                    for (String a : arr) {
                        if (a.startsWith("0x")) {
                            try {
                                this.imageFormat = Integer.parseInt(a.substring(2), 16);
                                readOk = true;
                                closeImageReader();
                                return;
                            } catch (NumberFormatException ex) {
                            }
                        }
                    }
                }
            }
        }
    }

    public Bitmap capture(float scale) throws InterruptedException {
        checkImageReader();
        if (!readOk) {
            synchronized (readLock) {
                readLock.wait(1000);
            }
        }
        try {
            Image image = imageReader.acquireLatestImage();
            if (image != null) {
                try {
                    cache = readBitmap(image);
                    cache = scaleBitmap(cache, screenWidth, screenHeight, 1.0f);
                } finally {
                    image.close();
                }
            }
        } catch (Throwable e) {
            Log.d("JsDroid", "capture: ", e);
            //异常，需要关闭并且再次创建image reader
            closeImageReader();
        }
        try {
            if (scale == 1) {
                return cache;
            } else if (cache != null) {
                return scaleBitmap(cache, cache.getWidth(), cache.getHeight(), scale);
            }
        } catch (Throwable e) {
            Log.d("JsDroid", "capture: ", e);
        }
        return null;
    }


    private Bitmap readBitmap(Image image) {
        if (image == null) {
            return null;
        }
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
        if (rotation == UiAutomation.ROTATION_FREEZE_0
                || rotation == UiAutomation.ROTATION_FREEZE_180) {
            screenWidth = devices.getNaturalWidth();
            screenHeight = devices.getNaturalHeight();
        } else {
            screenHeight = devices.getNaturalWidth();
            screenWidth = devices.getNaturalHeight();
        }
    }

    private void fireFrameUpdate() {
        getFrameFormatOnFirst();
        synchronized (readLock) {
            readLock.notifyAll();
        }
        checkImageReader();
        try {
            for (FrameListener frameListener : frameListeners) {
                frameListener.onFrameUpdate(Screens.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeImageReader() {
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
        checkImageReader();
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
            Log.d("JsDroid", "checkImageReader: ", e);
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
        ImageReader imageReader = ImageReader.newInstance(screenWidth, screenHeight, imageFormat == -1 ? PixelFormat.RGBA_8888 : imageFormat, 3);
        Rect screenRect = new Rect(0, 0, screenWidth, screenHeight);
        SurfaceControls.openTransaction();
        SurfaceControls.setDisplaySurface(display, imageReader.getSurface());
        SurfaceControls.setDisplayProjection(display, 0, screenRect, screenRect);
        SurfaceControls.setDisplayLayerStack(display, 0);
        SurfaceControls.closeTransaction();
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imageReader) {
                fireFrameUpdate();
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
