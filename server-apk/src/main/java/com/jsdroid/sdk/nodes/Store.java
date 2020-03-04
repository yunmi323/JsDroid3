package com.jsdroid.sdk.nodes;

public class Store<T> {
    private T obj;

    public T get() {
        return obj;
    }

    public void set(T obj) {
        this.obj = obj;
    }
}
