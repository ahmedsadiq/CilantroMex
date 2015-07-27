package com.dg.android.lcp.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import com.dg.android.lcp.activities.ApplicationController;

import android.os.Build;



public class ExceptionHandler implements UncaughtExceptionHandler {

    final static String TAG = "ExceptionHandler";

    public ExceptionHandler() {

    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        logException(ex);
    }

    public static void logException(Throwable ex) {

        // also log into standard output so that we don't need to extract the
        // log file during development.
        ex.printStackTrace();

        File errorLog = new File(ApplicationController.getPrivateDirectory() + "/wv_error.log");

        FileOutputStream fops = null;

        try {

            String logTitle = "TakaTaka Error Log\nAndroid OS " + Build.VERSION.RELEASE + " API Level " + Build.VERSION.SDK_INT
                    + "\nMANUFACTURER : " + Build.MANUFACTURER + "\nMODEL : " + Build.MODEL + "\nBRAND : " + Build.BRAND + "\n";
            boolean writeTitle = false;
            if (errorLog.exists()) {
                Long size = errorLog.length();
                if (size > (5 * (8 * 1024 * 1024))) {
                    errorLog.createNewFile();
                    writeTitle = true;
                }
            } else {
                writeTitle = true;
            }

            fops = new FileOutputStream(errorLog, true);

            if (writeTitle) {
                fops.write(logTitle.getBytes());
            }

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String logMessage = new Date() + " " + sw.toString();
            fops.write(logMessage.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fops != null) fops.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}
