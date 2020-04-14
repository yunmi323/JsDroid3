//
// Created by mayn on 2020-04-12.
//

#include "Log.h"
#include "Range.h"
#include "MPoint.h"

#define min(a, b)  (((a)<(b))?(a):(b))
#define max(a, b)  (((a)>(b))?(a):(b))

Range::Range(int x, int y) {
    this->left = this->right = x;
    this->top = this->bottom = y;
}

Range::~Range() {
}


bool Range::addPoint(int x, int y, int distance) {
    if (x + distance < left//点在矩形左方
        || x - distance > right//点在矩形右方
        || y + distance < top //点在矩形上方
        || y - distance > right //点在矩形下方
            ) {
        return false;
    }
    left = min(x, left);
    top = min(y, top);
    right = max(x, right);
    bottom = max(y, bottom);
    return true;
}

void Range::addPoint(int x, int y) {
    left = min(x, left);
    top = min(y, top);
    right = max(x, right);
    bottom = max(y, bottom);
}

void Range::merge(Range *other) {
    left = min(other->left, left);
    right = max(other->right, right);
    top = min(other->top, top);
    bottom = max(other->bottom, bottom);
    other->left = left;
    other->right = right;
    other->top = top;
    other->bottom = bottom;
    other->id = id;
}

static int count = 0;

#include <list>

using namespace std;

void
Range::scan(int *pixels, int *used, int width, int height, int x, int y, int color, int distance) {

    list<MPoint> pList;
    pList.push_back(MPoint(x, y));
    auto it = pList.begin();
    while (it != pList.end()) {
        auto p = *it;
        for (int dy = -distance; dy <= distance; dy++) {
            int pos_y = p.y + dy;
            if (pos_y < 0 || pos_y >= height)continue;
            int offsetPos = pos_y * width;
            for (int dx = -distance; dx <= distance; ++dx) {
                int pos_x = p.x + dx;
                if (pos_x < 0 || pos_x >= width)continue;
                int pos = pos_x + offsetPos;
                if (used[pos] == 1) {
                    continue;
                }
                used[pos] = 1;
                int c = pixels[pos];
                if (c == color) {
                    addPoint(pos_x, pos_y);
                    pList.push_back(MPoint(pos_x, pos_y));
                }
            }
        }
        it++;
    }

}