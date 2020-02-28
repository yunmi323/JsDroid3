package com.jsdroid.sdk.https;

import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class Https {
    private static class Single {
        static Https single = new Https();
    }

    public static Https getInstance() {

        return Single.single;
    }

    private Https() {

    }

    public String get(String url) {
        try {
            return connect(url, Connection.Method.GET).execute().body();
        } catch (IOException e) {
        }
        return null;
    }

    public String get(String url, Map params) {
        return get(url, null, params);
    }

    public String get(String url, Map headers, Map params) {
        Connection connect = connect(url, Connection.Method.GET);
        if (params != null) {
            for (Object key : params.keySet()) {
                if (key != null) {
                    Object value = params.get(key);
                    if (value == null) {
                        value = "";
                    }
                    connect.data(key.toString(), value.toString());
                }
            }
        }
        if (headers != null) {
            for (Object key : headers.keySet()) {
                if (key != null) {
                    Object value = headers.get(key);
                    if (value == null) {
                        value = "";
                    }
                    connect.header(key.toString(), value.toString());
                }
            }
        }
        try {
            return connect.execute().body();
        } catch (IOException e) {
        }
        return null;
    }

    public String post(String url, Map params) {
        return post(url, null, params);
    }

    public String post(String url, Map headers, Map params) {
        Connection connect = connect(url, Connection.Method.POST);
        if (params != null) {
            for (Object key : params.keySet()) {
                if (key != null) {
                    Object value = params.get(key);
                    if (value == null) {
                        value = "";
                    }
                    connect.data(key.toString(), value.toString());
                }
            }
        }
        if (headers != null) {
            for (Object key : headers.keySet()) {
                if (key != null) {
                    Object value = headers.get(key);
                    if (value == null) {
                        value = "";
                    }
                    connect.header(key.toString(), value.toString());
                }
            }
        }
        try {
            return connect.execute().body();
        } catch (IOException e) {
        }
        return null;
    }

    private Connection connect(String url, Connection.Method method) {
        return Jsoup.connect(url).ignoreContentType(true)
                .method(method)
                .ignoreHttpErrors(true)
                .validateTLSCertificates(true);
    }

    public boolean download(String url, String savePath) {
        Connection connect = connect(url, Connection.Method.GET);
        connect.maxBodySize(Integer.MAX_VALUE);
        connect.followRedirects(true);
        try (BufferedInputStream inputStream = connect.execute().bodyStream();
             FileOutputStream outputStream = new FileOutputStream(savePath);
        ) {
            IOUtils.copy(inputStream, outputStream);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public String upload(String url, String key, String file) {
        Connection connect = connect(url, Connection.Method.POST);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            connect.data(key, file, fileInputStream);
            return connect.execute().body();
        } catch (IOException e) {
        }
        return null;
    }
}
