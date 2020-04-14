//
// Created by mayn on 2020-04-12.
//

#ifndef JSDROID3_RANGE_H
#define JSDROID3_RANGE_H

#include <jni.h>

class Range {

public:
    Range(int x,int y);

    ~Range();

    int id;
    int left;
    int top;
    int right;
    int bottom;

    bool addPoint(int x, int y, int distance);

    void addPoint(int x, int y);

    void merge(Range *other);

    void scan(int *pixels, int *used, int width, int height, int x, int y, int color, int distance);


};


#endif //JSDROID3_RANGE_H
