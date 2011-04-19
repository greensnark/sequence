package com.zanthan.sequence.preferences;

import java.io.File;

/**
 *
 */
public class CurrentDirectory {

    private static File currentDirectory = null;

    public static File getCurrentDirectory() {
        return currentDirectory;
    }

    public static void setCurrentDirectory(File currentDirectory) {
        CurrentDirectory.currentDirectory = currentDirectory;
    }
}
