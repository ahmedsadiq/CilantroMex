package com.dg.android.lcp.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidUtil {

	public static String LOCALE_HEADER_VALUE = "es";

	// Live

	// public static String APPKEY = "CuZnCcJvNeFfScey";
	// public static final String ROOT_URL = "http://app.relevantmobile.com";
	// public static final String ROOT_URL_HTTPS =
	// "https://app.relevantmobile.com";
	// public static String BASE_URL = ROOT_URL + "/api/v1"; // Live
	// public static String BASE_URL_HTTPS = ROOT_URL_HTTPS + "/api/v1"; // Live
	//
	// public static String TERMS_OF_USE = ROOT_URL + "/terms_of_use";
	// public static String PRIVACY_POLICY = ROOT_URL + "/privacy_policy";
	// public static String FB_PAGE_URL = ROOT_URL + "/url/2/facebook-lcp";
	// public static String TWITTER_PAGE_URL = ROOT_URL +
	// "/url/2/twitter-lcp";
	//
	// public static String MENU_ZOES = ROOT_URL + "/url/2/menu-lcp";

	// Live

	// Trel
//	XqaTB174ZakEUhK4
	public static String APPKEY = "XqaTB174ZakEUhK4";
//	public static String BASE_URL = "http://trelevant.herokuapp.com/api/v1";
	public static String BASE_URL = "http://nfrel.herokuapp.com/api/v1";
	public static String BASE_URL_HTTPS = "https://trelevant.herokuapp.com/api/v1";
	public static String TERMS_OF_USE = "http://trelevant.herokuapp.com/terms_of_use?locale=es";
	public static String FB_PAGE_URL = "http://trelevant.herokuapp.com/url/4/facebook-lcp";
	public static String TWITTER_PAGE_URL = "http://trelevant.herokuapp.com/url/4/twitter-lcp";

	public static String MENU_ZOES = "http://trelevant.herokuapp.com/url/4/menu-lcp";

	// Trel
	
//		facebook : http://app.relevantmobile.com/url/2/facebook-lcp
//		twitter : http://app.relevantmobile.com/url/2/twitter-lcp
//		menu:  http://app.relevantmobile.com/url/2/menu-lcp
			
	public static void setTextViewAttributeBold(TextView textView, int size,
			String color, AssetManager assetManager/* , Typeface face */) {
		textView.setTextSize(size);
		color = "#" + color;
		textView.setTextColor(Color.parseColor(color));
		textView.setTypeface(null, Typeface.BOLD);
		// tv.setTextColor(Color.parseColor("#000000"))
	}

    final static String TAG = "AndroidUtil";
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@" + "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
            + "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");
  

    public static boolean hasStorage() {
        return hasStorage(true);
    }

    public static boolean hasStorage(boolean requireWriteAccess) {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (requireWriteAccess) {
                boolean writable = checkFsWritable();
                return writable;
            } else {
                return true;
            }
        } else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) { return true; }
        return false;
    }

    private static boolean checkFsWritable() {
        // Create a temporary file to see whether a volume is really writeable.
        // It's important not to put it in the root directory which may have a
        // limit on the number of files.
        String directoryName = Environment.getExternalStorageDirectory().toString() + "/DCIM";
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) { return false; }
        }
        File f = new File(directoryName, ".probe");
        try {
            // Remove stale file if any
            if (f.exists()) {
                f.delete();
            }
            if (!f.createNewFile()) { return false; }
            f.delete();
            return true;
        } catch (IOException ex) {
            ExceptionHandler.logException(ex);
            return false;
        }
    }
    
    public static void showMessageDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

	public static void showMessageDialogWithFinish(final Activity object,
			Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						object.finish();

					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

    public static void showToastMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
   static public  boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    // Fast Implementation
    public static StringBuilder inputStreamToString(InputStream is) throws IOException {
        String line = "";
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        while ((line = rd.readLine()) != null) {
            total.append(line);
        }

        rd.close();

        // Return full string
        return total;
    }
	public static void showMessageDialogWithNewIntent(final Activity curentActivity,Context context, String message, final Class newActivity,final String extras) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

						Intent i = new Intent(curentActivity, newActivity);
						if(extras!=null){
							Log.i("LOGINSIGNUP","=extras!=null in showMessageDialog");
						i.putExtra("OAuthError", true);
						}
						Log.i("LOGINSIGNUP","==after if in showMessageDialog");
						
						curentActivity.startActivity(i);
//						curentActivity.finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}



}
