package com.jsdroid.colors;

import android.graphics.Bitmap;

public class Img {
    private long mNativePtr;
    private int width;
    private int height;
    private Bitmap bitmap;

    static {
        System.loadLibrary("colors");
    }

    public static Img create(Bitmap bitmap) {
        Img img = nativeCreate(bitmap);
        img.width = bitmap.getWidth();
        img.height = bitmap.getHeight();
        img.bitmap = bitmap;
        return img;
    }

    private Img() {
    }

    public Img clone() {
        return nativeClone();
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(width, height, this.bitmap.getConfig());
        nativeGetBitmap(bitmap);
        return bitmap;
    }

    public void split(int threshold, int color1, int color2) {
        nativeSplit(threshold, color1, color2);
    }

    public void keepColor(int keepColor, int offset, int bgColor) {
        nativeKeepColor(keepColor, offset, bgColor);
    }

    public void removeColor(int removeColor, int offset, int bgColor) {
        nativeRemoveColor(removeColor, offset, bgColor);
    }

    public Range[] getRanges(int color, int pointDistance) {
        return nativeGetRanges(color, pointDistance);
    }

    public int getRangeHorizontalLineCount(int color, Range range) {
        if (range == null) {
            return 0;
        }
        return nativeGetRectHorizontalLineCount(color, range.left, range.top, range.right, range.bottom);
    }

    private static native Img nativeCreate(Bitmap image);

    //克隆
    private native Img nativeClone();

    //获得图片数据
    private native void nativeGetBitmap(Bitmap bitmap);

    private native void nativeRelease();


    /**
     * 二值化
     *
     * @param threshold 阀值(r+g+b)
     * @param color1    小于阀值的颜色
     * @param color2    大于阀值的颜色
     */
    private native void nativeSplit(int threshold, int color1, int color2);

    /**
     * 保留颜色
     *
     * @param keepColor
     * @param offset
     * @param bgColor
     */
    private native void nativeKeepColor(int keepColor, int offset, int bgColor);


    /**
     * 移除颜色
     *
     * @param removeColor
     * @param offset
     * @param bgColor
     */
    private native void nativeRemoveColor(int removeColor, int offset, int bgColor);


    private native Range[] nativeGetRanges(int color, int pointDistance);//获得指定颜色的所有范围

    /**
     * 在一个范围内，从上往下扫描，获取线条数量
     *
     * @param color
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    private native int nativeGetRectHorizontalLineCount(int color, int left, int top, int right, int bottom);

    /**
     * 找图
     *
     * @param img    要找的图
     * @param left   左
     * @param top    上
     * @param right  右
     * @param bottom 下
     * @param offset 色差
     * @param sim    相似度
     * @return
     */
    private native int[] nativeFindImg(Img img, int left, int top, int right, int bottom, int offset, float sim);

    /**
     * 多点找色
     * findMultiColor [[color,offset,x,y],[color,offset,x,y]],left,top,right,bottom
     *
     * @param colors
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    private native int[] nativeFindMultiColor(int[][] colors, int left, int top, int right, int bottom);


    @Override
    protected void finalize() throws Throwable {
        nativeRelease();
        super.finalize();
    }
}
