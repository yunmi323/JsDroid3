package com.jsdroid.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BoxLog {
    public static File getLogFile() {
        return new File("/data/local/tmp/jsd_box_log.txt");
    }

    public static void print(String text) {
        if (text == null) {
            return;
        }
        try (FileOutputStream out = new FileOutputStream(getLogFile(), true)) {
            out.write((text + "\n").getBytes());
        } catch (IOException e) {
        }
    }
}
