package com.jsdroid.pro;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jsdroid.colors.Img;
import com.jsdroid.colors.Range;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hk);
        try {
            test(bitmap);
        } catch (Throwable e) {
            Log.e("JsDroid", "err: ", e);
        }
    }

    private void test(Bitmap bitmap) {

        long time = System.currentTimeMillis();
        Img img = Img.create(bitmap);
        Log.e("JsDroid", "create use time: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        img.split(300, 0xffff0000, 0xffffffff);//57ms
        Log.e("JsDroid", "split use time: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        Range[] ranges = img.getRanges(0xffff0000, 1);
        Log.e("JsDroid", "get ranges use time: " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        Bitmap result = img.getBitmap();
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        if (ranges != null) {
            for (Range range : ranges) {
                canvas.drawRect(range.left, range.top, range.right, range.bottom, paint);
            }
            Log.e("JsDroid", ranges.length + "个range");
        } else {
            Log.e("JsDroid", "ranges is null");
        }
        Log.e("JsDroid", "getBitmap use time: " + (System.currentTimeMillis() - time));
        try (
                FileOutputStream out = new FileOutputStream(new File("/sdcard/uu.png"));
        ) {
            result.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.e("JsDroid", "end: ");
        } catch (Exception er) {
            Log.e("JsDroid", "onCreate: ", er);
        }
    }
}