package com.jsdroid.box;

import android.graphics.Bitmap;

import com.jsdroid.sdk.devices.Devices;
import com.jsdroid.sdk.events.Events;
import com.jsdroid.sdk.screens.Screens;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.WebSocket;

import java.io.ByteArrayOutputStream;

public class WebScreen implements Screens.FrameListener {
    WebSocket webSocket;

    public WebScreen(WebSocket webSocket) {
        this.webSocket = webSocket;
        Screens.getInstance().addListener(this);
        touchCallback();
    }

    private void touchCallback() {
        //模拟控制
        webSocket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                try {
                    int type = bb.get();
                    int x = bb.getShort();
                    int y = bb.getShort();
                    //screenX*scale=x
                    float scale = scale();
                    switch (type) {
                        case 1:
                            Events.getInstance().touchDown((int) (x / scale + 0.5f), (int) (y / scale + 0.5f));
                            break;
                        case 2:
                            Events.getInstance().touchMove((int) (x / scale + 0.5f), (int) (y / scale + 0.5f));
                            break;
                        case 3:
                            Events.getInstance().touchUp((int) (x / scale + 0.5f), (int) (y / scale + 0.5f));
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private float scale() {
        try {
            int screenWidth = Devices.getInstance().getWidth();
            int screenHeight = Devices.getInstance().getHeight();
            float scale;
            //横屏缩放到960，竖屏缩放到800
            if (screenWidth > screenHeight) {
                scale = 720f / screenWidth;
            } else {
                scale = 720f / screenHeight;
            }
            if (scale > 1) {
                scale = 1;
            }
            return scale;
        } catch (Exception e) {
            return 1f;
        }
    }


    @Override
    public void onFrameUpdate(Screens screens) {
        if (webSocket.isOpen()) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Bitmap capture = screens.capture(scale());
                capture.compress(Bitmap.CompressFormat.JPEG, 80, out);
                byte[] bytes = out.toByteArray();
                webSocket.send(bytes);
            } catch (InterruptedException e) {
            }
        } else {
            Screens.getInstance().removeListener(this);
        }
    }
}
