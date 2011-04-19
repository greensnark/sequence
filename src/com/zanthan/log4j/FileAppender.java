package com.zanthan.log4j;

import java.io.IOException;
import java.io.File;

/**
 * An extension of FileAppender that creates the directory to contain the log file if it doesn't
 * already exist.
 */
public class FileAppender extends org.apache.log4j.FileAppender {

    /**
     * <p>Sets and <i>opens</i> the file where the log output will
     * go. The specified file must be writable.
     * <p/>
     * <p>If there was already an opened file, then the previous file
     * is closed first.
     * <p/>
     * <p><b>Do not use this method directly. To configure a FileAppender
     * or one of its subclasses, set its properties one by one and then
     * call activateOptions.</b>
     *
     * @param fileName The path to the log file.
     * @param append If true will append to fileName. Otherwise will
     * truncate fileName.
     */
    public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
            throws IOException {
        // Before calling super version make sure directory that will contain the file exists. Create it,
        // and its parents, if it doesn't.
        File logFile = new File(fileName);
        File logFileDirectory = logFile.getParentFile();
        if (logFileDirectory != null) {
            if (!logFileDirectory.exists()) {
                logFileDirectory.mkdirs();
            }
        }
        super.setFile(fileName, append, bufferedIO, bufferSize);
    }
}
