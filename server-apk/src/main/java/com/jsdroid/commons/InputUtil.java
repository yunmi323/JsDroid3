package com.jsdroid.commons;


import java.util.List;

public class InputUtil {

    public static List<String> list() {
        return com.jsdroid.app_hidden_api.InputUtil.list();
    }

    public static void setIME(String id) {
        com.jsdroid.app_hidden_api.InputUtil.setInputMethod(id);
    }

}
