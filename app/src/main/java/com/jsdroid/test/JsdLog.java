package com.jsdroid.test;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JsdLog {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS: ");

    static void clearIfOutOfSize(int size) {
        File file = new File(JsdApp.getInstance().getLogFile());
        if (file.exists()) {
            if (file.length() > size) {
                String save = read(file, size / 2);
                file.delete();
                if (save != null) {
                    FileIOUtils.writeFileFromString(file, save);
                }
            }
        }
    }

    static String read(File file, int pos) {
        if (file.exists() && pos < file.length()) {
            try (RandomAccessFile accessFile = new RandomAccessFile(file, "rw");) {
                accessFile.seek(pos);
                byte[] buff = new byte[(int) (file.length() - pos)];
                accessFile.readFully(buff);
                return new String(buff, "utf-8");
            } catch (IOException e) {
            }
        }
        return null;
    }


    public static synchronized void print(String text) {
        //大于20万文字，则清除1半，最多显示10万文字
        clearIfOutOfSize(200000);
        String log = simpleDateFormat.format(new Date()) + text + "\n";
        FileIOUtils.writeFileFromString(JsdApp.getInstance().getLogFile(), log, true);
    }

    public static synchronized String read() {
        return FileIOUtils.readFile2String(JsdApp.getInstance().getLogFile());
    }

    public static synchronized void clear() {
        FileUtils.delete(JsdApp.getInstance().getLogFile());
    }
}
