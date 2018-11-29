package com.barcodereader.utils;

import java.io.File;

/**
 * Created by shivappa.battur on 28/11/2018
 */
public class FileUtils {
    private static FileUtils INSTANCE;

    private FileUtils() {
    }

    public static FileUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileUtils();
        }
        return INSTANCE;
    }

    public void createDirectory(String dirName) {
        File file = new File(dirName);
        if (!file.exists())
            file.mkdir();
    }
}
