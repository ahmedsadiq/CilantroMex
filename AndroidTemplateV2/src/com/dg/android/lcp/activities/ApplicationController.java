package com.dg.android.lcp.activities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Application;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import com.dg.android.lcp.utils.ExceptionHandler;



public class ApplicationController extends Application {

    final static String TAG = "ApplicationController";
    final static String APP_ID = "PYYznPetBdoHFpxM";
    //final static String CHAIN_ID = "780";
    
    private static File privateDirectory;

    public static boolean firstRun = true;

    public static boolean isFirstRun() {
        if (firstRun) {
            firstRun = false;
            return true;
        } else {
            return false;
        }
    }

    public static File getPrivateDirectory() {
        return privateDirectory;
    }

    private static String preferencesFileName = "gobbles";

    public static String getPreferencesFileName() {
        return preferencesFileName;
    }



    private static Properties properties;

    public String getProperty(String property) {
        String result = null;
        if (properties != null) result = properties.getProperty(property);
        return result;
    }

    public void initialize() {

        Log.v(TAG, "Android OS " + Build.VERSION.RELEASE + " API Level " + Build.VERSION.SDK_INT + "\nMANUFACTURER : " + Build.MANUFACTURER
                + "\nMODEL : " + Build.MODEL + "\nBRAND : " + Build.BRAND + "\n");

        privateDirectory = getExternalFilesDir(null);
        Resources resources = this.getResources();
        InputStream rawResource = resources.openRawResource(R.raw.takataka);

        properties = new Properties();
        try {
            properties.load(rawResource);
        } catch (IOException e) {
            ExceptionHandler.logException(e);
            e.printStackTrace();
        }


    }

    @Override
    public void onCreate() {

        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        Log.v(TAG, "Starting TakaTaka.....");

        initialize();

    }

}