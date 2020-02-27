package com.jsdroid.app_hidden_api;


import java.util.WeakHashMap;

public class Hide {
    class A{
        String id;

        public A(String id) {
            this.id = id;
        }
    }
    public static void main(String[] args) throws InterruptedException {
        WeakHashMap<String, Object> weakHashMap = new WeakHashMap<>();
        String key1="user1";
        for (int i = 0; i < 100; i++) {
            weakHashMap.put(key1, new Hide());
            weakHashMap.put(new String("key"),new Hide());
            weakHashMap.put(new String("key2"),new Hide());
            weakHashMap.put("key"+i,new Hide());
            System.gc();
        }
        System.gc();
        Thread.sleep(500);
        System.out.println(weakHashMap.size());
    }

}
