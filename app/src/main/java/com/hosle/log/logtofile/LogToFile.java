package com.hosle.log.logtofile;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LogToFile {

    private volatile static ConcurrentLinkedQueue<String> mLogCacheQueue = new ConcurrentLinkedQueue<>();
    private static ScheduledExecutorService mService = new ScheduledThreadPoolExecutor(1);
    private static LinkedList<ScheduledFuture<Integer>> mTaskFutures = new LinkedList<>();

    private static final int WRITING_DELAY = 20;
    private static final int MAX_CACHE_SIZE = 20;
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Object mLock = new Object();

    private static String mRootDir = Environment.DIRECTORY_DOWNLOADS;
    private static String mDirName = "SimpleLogToFile";
    private static String mFileName = "LogToFile.log";

    public static void setFileName(String logFileName) {
        mFileName = logFileName;
    }

    private static class SaveToFileTask implements Callable<Integer> {

        private List<String> writingLogRecords = null;

        public SaveToFileTask(List<String> logRecords) {
            this.writingLogRecords = logRecords;
        }

        @Override
        public Integer call() throws Exception {
            File root = new File(Environment.getExternalStoragePublicDirectory(mRootDir), mDirName);
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, mFileName);
            file.createNewFile();
            int amountSaved = 0;
            boolean success = false;

            if (writingLogRecords != null) {
                amountSaved = writingLogRecords.size();
                FileWriter outputStreamWriter = null;

                if (file.exists()) {
                    try {
                        outputStreamWriter = new FileWriter(file, true);
                        outputStreamWriter.append("\n\n======== Log to File ("+ getCurrentReadableTs() +") ========");
                        int i = 0;
                        while (i < writingLogRecords.size()) {
                            outputStreamWriter.append("\n");
                            outputStreamWriter.append(writingLogRecords.get(i));
                            i++;
                        }

                        success = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return 0;
                    } finally {
                        if (outputStreamWriter != null) {
                            outputStreamWriter.flush();
                            outputStreamWriter.close();
                        }
                    }
                }
            }

            if (success){
                mLogCacheQueue.removeAll(writingLogRecords);
            }

            return amountSaved;
        }
    }

    private static String getCurrentReadableTs() {
        return mDateFormat.format(new Date());
    }

    public static void logToFile(String content) {
        mLogCacheQueue.add(content);

        synchronized (mLock) {
            Iterator<ScheduledFuture<Integer>> iterator = mTaskFutures.listIterator();
            while (iterator.hasNext()) {
                ScheduledFuture<Integer> task = iterator.next();
                task.cancel(true);
                iterator.remove();
            }
        }

        ScheduledFuture<Integer> newFuture;
        List<String> data = Arrays.asList(mLogCacheQueue.toArray(new String[0]));
        if (mLogCacheQueue.size() >= MAX_CACHE_SIZE) {
            newFuture = mService.schedule(new SaveToFileTask(data), 0, TimeUnit.SECONDS);
        } else {
            newFuture = mService.schedule(new SaveToFileTask(data), WRITING_DELAY, TimeUnit.SECONDS);
        }
        synchronized (mLock) {
            mTaskFutures.add(newFuture);
        }
    }
}
