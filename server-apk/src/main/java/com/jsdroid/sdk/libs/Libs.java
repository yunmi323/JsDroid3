package com.jsdroid.sdk.libs;

import com.jsdroid.commons.LibraryUtil;

import java.io.IOException;
import java.util.List;

public class Libs {

    public static final int SDK_INT = LibraryUtil.SDK_INT;

    public static List<String> extractLibFile(String apkFile, String outDir) throws IOException {

        return LibraryUtil.extractLibFile(apkFile, outDir);
    }

    public static List<String> getSupportABIs() {
        return LibraryUtil.getSupportABIs();
    }
}
