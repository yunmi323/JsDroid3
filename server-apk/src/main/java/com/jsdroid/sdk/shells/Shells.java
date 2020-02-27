package com.jsdroid.sdk.shells;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Shells {
    private static class Single {
        static Shells https = new Shells();
    }

    public static Shells getInstance() {

        return Single.https;
    }

    private Shells() {

    }

    public String exec(String shell) {
        return exec(shell, true);
    }

    private static String exec(String command, boolean output) {
        StringWriter sw = new StringWriter();
        try {
            Process process = Runtime.getRuntime().exec("sh");
            Thread out = null;
            Thread err = null;
            if (output) {
                PrintWriter pw = new PrintWriter(sw);
                out = new Thread(new StreamGobbler(process.getInputStream(), pw));
                err = new Thread(new StreamGobbler(process.getErrorStream(), pw));
                out.start();
                err.start();
            }
            PrintWriter stdin = new PrintWriter(process.getOutputStream());
            stdin.println(command);
            stdin.println("exit");
            stdin.flush();
            stdin.close();
            process.waitFor();
            if (output) {
                out.join();
                err.join();
            }
            return sw.toString();
        } catch (IOException e) {
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private static class StreamGobbler implements Runnable {
        private final InputStream is;
        private final PrintWriter pw;

        StreamGobbler(InputStream is, PrintWriter pw) {
            this.is = new BufferedInputStream(is);
            this.pw = pw;
        }

        @Override
        public void run() {
            try (
                    BufferedReader br = new BufferedReader(new InputStreamReader(is))
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    synchronized (pw) {
                        pw.println(line);
                        pw.flush();
                    }
                }
            } catch (IOException e) {
            }
        }
    }
}
