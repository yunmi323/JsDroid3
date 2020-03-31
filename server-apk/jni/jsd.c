#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>

#ifndef eprintf
#define eprintf(...) __android_log_print(ANDROID_LOG_ERROR,"@",__VA_ARGS__)

#endif
/*
void deal(JNIEnv *env, jobject jbitmap) {
    void *srcPixels = 0;
    //native层的Bitmap对象信息
    AndroidBitmapInfo bitmapInfo;
//    memset(&bitmapInfo, 0, sizeof(bitmapInfo));
    AndroidBitmap_getInfo(env, jbitmap, &bitmapInfo);
    void *pixels = NULL;
    int res = AndroidBitmap_lockPixels(env, jbitmap, &pixels);
    int x = 0, y = 0;
    for (y = 0; y < bitmapInfo.height; ++y) {
        for (x = 0; x < bitmapInfo.width; ++x) {
            void *pixel = NULL;
            if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
                pixel = ((uint32_t *) pixels) + y * bitmapInfo.width + x;
            } else if (bitmapInfo.format == ANDROID_BITMAP_FORMAT_RGB_565) {
                pixel = ((uint16_t *) pixels) + y * bitmapInfo.width + x;
            }
        }
    }
    AndroidBitmap_unlockPixels(env, jbitmap);
}
*/