package com.jsdroid.box;

import android.graphics.Bitmap;
import android.util.Log;

import com.jsdroid.api.IInput;
import com.jsdroid.api.IJsDroidApp;
import com.jsdroid.api.IJsDroidShell;
import com.jsdroid.sdk.nodes.Node;
import com.jsdroid.sdk.nodes.Nodes;
import com.jsdroid.sdk.screens.ScreenUtil;
import com.jsdroid.sdk.screens.Screens;
import com.jsdroid.server.JsDroidService;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.DataSink;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BoxServer {
    void sendData(AsyncHttpServerResponse response, JSONObject body) throws JSONException {
        JSONObject resp = new JSONObject();
        resp.put("code", 0);
        resp.put("data", body);
        response.send(resp);
    }

    void sendSuccess(AsyncHttpServerResponse response, String msg) throws JSONException {
        JSONObject resp = new JSONObject();
        resp.put("code", 0);
        resp.put("msg", msg);
        response.send(resp);
    }

    void sendFail(AsyncHttpServerResponse response, String err) throws JSONException {
        JSONObject resp = new JSONObject();
        resp.put("code", 0);
        resp.put("err", err);
        response.send(resp);
    }

    class ScriptFileReceiver implements MultipartFormDataBody.MultipartCallback, DataCallback, CompletedCallback, IJsDroidApp {
        FileOutputStream fileOutputStream;
        AsyncHttpServerResponse response;
        String saveFile = "/data/local/tmp/script.jsd";
        IJsDroidShell shell;
        MultipartFormDataBody body;

        public ScriptFileReceiver(MultipartFormDataBody body, AsyncHttpServerResponse response) {
            this.response = response;
            this.body = body;
        }

        @Override
        public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
            byte[] bytes = bb.getAllByteArray();
            try {
                fileOutputStream.write(bytes);
            } catch (IOException e) {
            }
        }

        @Override
        public void onPart(Part part) {
            body.setEndCallback(this);
            body.setDataCallback(this);
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                }
            }
            try {
                fileOutputStream = new FileOutputStream(saveFile);
            } catch (Exception e) {
            }

        }

        @Override
        public void onCompleted(Exception ex) {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                }
            }
            if (ex == null) {
                try {
                    shell = JsDroidService.getInstance().getShell(this);
                    if (shell.isRunning()) {
                        try {
                            sendSuccess(response, "已经在运行！");
                            return;
                        } catch (JSONException e) {
                        }
                    }
                    shell.runScript(saveFile);
                } catch (InterruptedException e) {
                }
                try {
                    sendSuccess(response, "运行成功！");
                } catch (JSONException e) {
                }
            } else {
                //发送错误
                try {
                    sendFail(response, "运行错误！");
                } catch (JSONException e) {
                }

            }

        }

        @Override
        public void toast(String text) throws InterruptedException {

        }

        @Override
        public String getPackage() throws InterruptedException {
            return "http-script";
        }

        @Override
        public String getVersion() throws InterruptedException {
            return "1.0";
        }

        @Override
        public void print(String text) throws InterruptedException {
            BoxLog.print(text);
        }

        @Override
        public void onScriptStart() throws InterruptedException {
            BoxLog.print("开始运行");
        }

        @Override
        public void onScriptStop(String result) throws InterruptedException {
            BoxLog.print(result);
            BoxLog.print("停止运行");
        }

        @Override
        public void onVolumeDown(boolean scriptRunning) throws InterruptedException {

        }

        @Override
        public IInput getInput() throws InterruptedException {
            return null;
        }

        @Override
        public void loadScript(String file) throws InterruptedException {

        }

        @Override
        public String readConfig(String key, String defaultValue) throws InterruptedException {
            return null;
        }
    }

    public BoxServer(final int port) {
        AsyncHttpServer httpServer = new AsyncHttpServer();
        httpServer.post("/api/runScript", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                AsyncHttpRequestBody body = request.getBody();
                if (body instanceof MultipartFormDataBody) {
                    MultipartFormDataBody fileBody = (MultipartFormDataBody) body;
                    ScriptFileReceiver scriptFileReceiver = new ScriptFileReceiver(fileBody, response);
                    fileBody.setMultipartCallback(scriptFileReceiver);
                    fileBody.setDataCallback(scriptFileReceiver);
                    fileBody.setEndCallback(scriptFileReceiver);
                }
            }
        });
        httpServer.get("/api/stopScript", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                try {
                    sendSuccess(response, "已停止");
                } catch (JSONException e) {
                }
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 200);

            }
        });

        httpServer.get("/api/node", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                List<Node> rootNodes = Nodes.getInstance().getRootNodes();
                JSONArray jsonArray = new JSONArray();
                if (rootNodes != null) {
                    for (Node rootNode : rootNodes) {
                        try {
                            jsonArray.put(rootNode.getJson());
                        } catch (JSONException e) {
                        }
                    }
                }
                String data = jsonArray.toString();
                Log.d("JsDroid", "node: " + data.length());
                response.send("application/json", data);

            }
        });
        httpServer.get("/api/snap", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                Bitmap screenshot = null;
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    screenshot = Screens.getInstance().capture(ScreenUtil.getScale(300));
                    screenshot.compress(Bitmap.CompressFormat.JPEG, 80, out);
                    response.send("image/jpeg", out.toByteArray());
                } catch (Exception e) {
                }

            }
        });
        httpServer.get("/api/capture", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                Bitmap screenshot = null;
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    screenshot = Screens.getInstance().capture();
                    screenshot.compress(Bitmap.CompressFormat.PNG, 100, out);
                    response.send("image/png", out.toByteArray());
                } catch (Exception e) {
                }

            }
        });


        httpServer.get("/api/log", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                //获取最新5条日志
                response.sendFile(BoxLog.getLogFile());
            }
        });

        httpServer.websocket("/api/screen", new AsyncHttpServer.WebSocketRequestCallback() {
            @Override
            public void onConnected(WebSocket webSocket, AsyncHttpServerRequest request) {
                new WebScreen(webSocket);
            }
        });
        httpServer.get("/index.html", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.send("text/html", "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Title</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "\n" +
                        "<h1>hello worlds</h1>\n" +
                        "<form action=\"/api/runScript\" method=\"post\" enctype=\"multipart/form-data\">\n" +
                        "    <p><input type=\"file\" name=\"upload\"></p>\n" +
                        "    <p><input type=\"submit\" value=\"submit\"></p>\n" +
                        "</form>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
            }
        });
        httpServer.listen(port);
    }

}
