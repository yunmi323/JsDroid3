package com.jsdroid.commons;

import android.os.Build;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LibraryUtil {

    /**
     * 至少安卓版本号为14，即安卓4.0
     *
     * @param classLoader
     * @param libFile
     * @throws Exception
     */
    public static void addNativeLibDir(ClassLoader classLoader, File libFile) throws Exception {
        //dalvik.system.DexPathList
        Object pathList = getPathList(classLoader);
        //获取当前类的属性
        if (Build.VERSION.SDK_INT >= 26) {
            //需要替换 dalvik.system.DexPathList.NativeLibraryElement nativeLibraryPathElements[]
            //NativeLibraryElement(Dir)
            Class<?> aClass = Class.forName("dalvik.system.DexPathList$NativeLibraryElement");
            Constructor<?> constructor = aClass.getConstructor(File.class);
            constructor.setAccessible(true);
            Object needAddElement = constructor.newInstance(libFile);
            Field nativeLibraryPathElementsFile = pathList.getClass().getDeclaredField("nativeLibraryPathElements");
            nativeLibraryPathElementsFile.setAccessible(true);
            Object nativeLibraryPathElements = nativeLibraryPathElementsFile.get(pathList);
            Object newElements = arrAdd(nativeLibraryPathElements, needAddElement);
            nativeLibraryPathElementsFile.set(pathList, newElements);
        } else if (Build.VERSION.SDK_INT >= 23) {
            //需要替换 dalvik.system.DexPathList.Element nativeLibraryPathElements[]
            Class<?> aClass = Class.forName("dalvik.system.DexPathList$Element");
            //File,boolean,File,dalvik.system.DexFile
            Constructor<?> constructor = aClass.getConstructor(File.class, boolean.class, File.class, Class.forName("dalvik.system.DexFile"));
            constructor.setAccessible(true);
            Object needAddElement = constructor.newInstance(libFile, true, null, null);
            Field nativeLibraryPathElementsFile = pathList.getClass().getDeclaredField("nativeLibraryPathElements");
            nativeLibraryPathElementsFile.setAccessible(true);
            Object nativeLibraryPathElements = nativeLibraryPathElementsFile.get(pathList);
            Object newElements = arrAdd(nativeLibraryPathElements, needAddElement);
            nativeLibraryPathElementsFile.set(pathList, newElements);
        } else {
            //小于22，直接替换即可
            Field nativeLibraryDirectoriesField = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
            nativeLibraryDirectoriesField.setAccessible(true);
            Object nativeLibraryDirectories = nativeLibraryDirectoriesField.get(pathList);
            Object newFiles = arrAdd(nativeLibraryDirectories, libFile);
            nativeLibraryDirectoriesField.set(pathList, newFiles);
        }
    }

    private static Object arrAdd(Object arr, Object add) {
        int length = Array.getLength(arr);
        Object newElements = Array.newInstance(add.getClass(), length + 1);
        Array.set(newElements, 0, add);
        for (int i = 1; i < length + 1; i++) {
            Array.set(newElements, i, Array.get(arr, i - 1));
        }
        return newElements;
    }

    private static Object getPathList(Object obj) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(obj, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    private static Object getField(Object obj, Class cls, String str) throws NoSuchFieldException, IllegalAccessException {
        Field declaredField = cls.getDeclaredField(str);
        declaredField.setAccessible(true);
        return declaredField.get(obj);
    }

    public static List<String> extractLibFile(String apkFile, String outDir) throws IOException {
        List<String> supportABIs = getSupportABIs();
        List<String> libFiles = new ArrayList<>();
        try (
                FileInputStream fileInput = new FileInputStream(apkFile);
                ZipInputStream zipInput = new ZipInputStream(fileInput);

        ) {
            while (true) {
                //lib/x86/libxxx.so
                ZipEntry nextEntry = zipInput.getNextEntry();
                if (nextEntry == null) {
                    break;
                }
                String name = nextEntry.getName();
                if (name.endsWith(".so")) {
                    File file = new File(name);
                    String libFile = file.getName();

                    //获取abi，判断是否在支持
                    File abiDir = file.getParentFile();
                    if (abiDir == null) {
                        continue;
                    }
                    //判断是否为lib文件夹
                    File libDir = abiDir.getParentFile();
                    if (libDir == null) {
                        continue;
                    }

                    if (!libDir.getName().equals("lib")) {
                        continue;
                    }

                    if (libDir.getParentFile() != null) {
                        if (!libDir.getParent().equals(".")) {
                            continue;
                        }
                    }

                    String abi = abiDir.getName();
                    //判断是否为支持的abi
                    if (!supportABIs.contains(abi)) {
                        continue;
                    }
                    //已经解压同名的so文件，并且当前so的abi不是默认abi，则不再继续解压，优选支持默认的abi
                    if (libFiles.contains(libFile) && (!supportABIs.get(0).equals(abi))) {
                        continue;
                    }
                    Log.d("jsdroid", "extractLibFile abi:" + abi + " lib:" + libFile);
                    new File(outDir, libFile).delete();
                    //解压so到outDir
                    try (FileOutputStream libOutput = new FileOutputStream(new File(outDir, libFile))) {
                        IOUtils.copy(zipInput, libOutput);
                        libFiles.add(libFile);
                    } catch (IOException e) {
                    }
                }
            }
        }

        return libFiles;
    }

    //只提供x86或者armv7支持
    public static List<String> getSupportABIs() {

        List<String> ret = new LinkedList<>();
        try {
            if (!ret.contains(Build.CPU_ABI)) {
                if (Build.CPU_ABI != null)
                    ret.add(Build.CPU_ABI);
            }
        } catch (Throwable ignore) {
        }
        try {
            if (!ret.contains(Build.CPU_ABI2)) {
                if (Build.CPU_ABI2 != null)
                    ret.add(Build.CPU_ABI2);
            }
        } catch (Throwable ignore) {
        }
        try {
            if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (String supportedAbi : Build.SUPPORTED_ABIS) {
                    if (supportedAbi != null) {
                        if (!ret.contains(supportedAbi)) {
                            ret.add(supportedAbi);
                        }
                    }

                }
            }
        } catch (Throwable ignore) {
        }
        Iterator<String> iterator = ret.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.trim().isEmpty()) {
                iterator.remove();
            }
        }


        return ret;
    }
    public static final int SDK_INT = Build.VERSION.SDK_INT
            + ("REL".equals(Build.VERSION.CODENAME) ? 0 : 1);

}
