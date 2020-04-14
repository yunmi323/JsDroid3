//
// Created by mayn on 2020-04-11.
//
#include <jni.h>
#include <list>
#include "Range.h"

using namespace std;
#ifndef JSDROID3_IMG_H
#define JSDROID3_IMG_H
#define ColorA(color) ((color & 0xFF000000) >> 24)
#define ColorR(color) ((color & 0x00FF0000) >> 16)
#define ColorG(color) ((color & 0x0000FF00) >> 8)
#define ColorB(color) (color & 0x000000FF)

#include "Log.h"

class Img {
private:
    jobject mObject;
    JNIEnv *env;
    int32_t *pixels;
    int width;
    int height;


public:
    Img();

    ~Img();

    void setBitmap(JNIEnv *env, jobject bitmap);

    void getBitmap(JNIEnv *env, jobject bitmap);

    void copy(Img *other);

    void split(int threshold, int color1, int color2);

    void keepColor(int color, int offsetColor, int bgColor);

    void removeColor(int color, int offsetColor, int bgColor);

    void bind(JNIEnv *env, jobject obj);

    void unbind();

    void getRanges(list<Range *> &rangeList, int color, int point_distance);

    int getRectHorizontalLineCount(int color,
                                int left, int top, int right, int bottom );
};

#endif //JSDROID3_IMG_H
