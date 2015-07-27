package com.dg.android.lcp.objects;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


public class SessionManager {

    private static HttpClient httpClient;
    static double latitude = 0.0;
    static double longitude = 0.0;
    static boolean isBackpress = false;
    static boolean isBackpressreward = false;

    public static HttpClient getHttpClient() {
        if (httpClient == null) httpClient = new DefaultHttpClient();
        return httpClient;
    }

    public static void login(Context c, String authToken) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("authToken", authToken);
        editor.commit();
    }

    public static void logout(Context c) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("authToken", null);
        editor.clear();
        editor.commit();
    }

    public static void setNextActivity(Context c, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("activityValue", value);
        editor.commit();
    }

    public static String getNextActivity(Context c) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        if (pref.getString("activityValue", null) != null) {
            String activityVal = pref.getString("activityValue", null);
            return activityVal;
        } else
            return null;
    }

    public static String getOffer(Context c) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        if (pref.getString("Offer", null) != null) {
            String offer = pref.getString("Offer", null);
            return offer;
        } else
            return null;
    }

    public static String getRestaurant(Context c) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        if (pref.getString("Restaurant", null) != null) {
            String offer = pref.getString("Restaurant", null);
            return offer;
        } else
            return null;
    }

    public static void setRestaurantAndOffer(Context c, String rest, String offer) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Restaurant", rest);
        editor.putString("Offer", offer);
        editor.commit();
    }

    public static boolean isUserLoggedIn(Context ctx) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (pref.getString("authToken", null) != null)
            return true;
        else
            return false;
    }

    public static String getLoggedInToken(Context ctx) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String userId = pref.getString("authToken", null);
        return userId;
    }
    
    public static void setAppStart(Context ctx, boolean isChecked) {
        SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("appStart", isChecked);
        editor.commit();
    }

    public static boolean getAppStart(Context ctx) { 
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getBoolean("appStart", false);
    }
    
    public static void setlatitude(double lat) {
        latitude = lat;
    }

    public static void setlongitude(double lon) {
        longitude = lon;
    }

    public static double getlatitude() {
        return latitude;
    }

    public static double getlongitude() {
        return longitude;
    }

    public static void setBackpress(boolean backpress) {
        isBackpress = backpress;
    }

    public static boolean getbackpress() {
        return isBackpress;
    }

    public static void setBackpressReward(boolean backpress) {
        isBackpressreward = backpress;
    }

    public static boolean getbackpressReward() {
        return isBackpressreward;
    }
    
    
    public static String getResId(Context ctx) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String regId = pref.getString("regId", null);
        Log.i("Session","getResId()========"+regId);
        return regId;
    }

    
    public static void setResId(Context c, String resId) {
    	Log.i("Session","setResId()======="+resId);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", resId);
        editor.commit();
    }
    
  

    

}
