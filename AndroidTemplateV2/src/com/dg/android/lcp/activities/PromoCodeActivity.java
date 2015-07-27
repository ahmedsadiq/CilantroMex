package com.dg.android.lcp.activities;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.utils.AndroidUtil;
import com.dg.android.lcp.utils.ServerRequest;

public class PromoCodeActivity extends Activity {

	Button crepeme;
	ProgressDialog progressDialog;
	Context context;
	String errorMessage;
	Location myLocation;
	String statusstr;
	String messagestr;
	TextView not_working;
	String parameter;
	JSONObject responseJson;
	public static String forceValue = "";
	public static String TAG = "PromoCodeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promo_code_view);
		context = this;
		crepeme = (Button) findViewById(R.id.crepe_me);
		not_working = (TextView) findViewById(R.id.not_working);
		not_working.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { getString(R.string.support_email)});
				intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
				final PackageManager pm = getPackageManager();
				final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
				ResolveInfo best = null;
				for (final ResolveInfo info : matches)
					if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
						best = info;
				if (best != null)
					intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
				startActivity(intent);

			}
		});
		crepeme.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			
			submitPromo();

			}
		});
	}

	public void submitPromo(){
		String promocode = ((EditText) findViewById(R.id.promocode)).getText().toString();
		parameter = "appkey=" + ApplicationController.APP_ID 
				+ "&auth_token=" + SessionManager.getLoggedInToken(context) 
				+ "&code="+promocode 
				+ "&force=" + forceValue
				+ "&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//				+ "&locale="+getString(R.string.locale_header_value);
        
		new SubmitPromoCodeAsyncTask(AndroidUtil.BASE_URL_HTTPS + "/promocode", parameter).execute();
//		new SubmitPromoCodeAsyncTask(getString(R.string.https_url) + "/promocode", parameter).execute();
	}
	private class SubmitPromoCodeAsyncTask extends AsyncTask<String, Void, Void> {
		String url;
		String param;

		public SubmitPromoCodeAsyncTask(String url, String param) {
			this.url = url;
			this.param = param;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorMessage = "";
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage(getString(R.string.wait));
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			try {
				
	            if (errorMessage != null && errorMessage.length() > 0 &&errorMessage.equals("401")) {
	                AndroidUtil.showMessageDialogWithNewIntent(PromoCodeActivity.this, context, getString(R.string.errorMessage401), LoginSignupActivity.class, "true");
	            }
	            else if (errorMessage != null && errorMessage.length() > 0)
			 {
				 progressDialog.dismiss();
			 AndroidUtil.showMessageDialog(context, errorMessage);
			 errorMessage="";
			 }
			 else {
				if(responseJson.getBoolean("stackable")==false){
						progressDialog.dismiss();
						forceValue ="true";
						new AlertDialog.Builder(context).setMessage(responseJson.getString("notice")).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int id) {
				            	submitPromo();
				            }
				        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				            @Override
				            public void onClick(DialogInterface dialog, int which) {
                             ((Activity)context).finish();
				            }

				        }).create().show();
						
					 }
					else
					{
						 progressDialog.dismiss();
						 AndroidUtil.showMessageDialogWithFinish(PromoCodeActivity.this,context, responseJson.getString("notice"));
					}
				
			 }
			 } catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}

		@Override
		protected Void doInBackground(String... params) {
			submitPromoCode();
			return null;
		}

		private void submitPromoCode() {
			Log.i(TAG,"URL = "+url+"  Param= "+param);
			
			responseJson = ServerRequest.sendPostRequestWithoAuthToken(url, param);
			Log.i(TAG, responseJson.toString());

			 try {
			 if (responseJson != null) {
			 if (responseJson.getBoolean("status") == true) {
			 // if successfully get the info of user from server
			 errorMessage = "";
			
			 }
	        else if (responseJson.getBoolean("status") == false&&!(responseJson.isNull("errorMsg"))) {
	              Log.i("Response Json Failure:", "" + responseJson.toString());
	               errorMessage=responseJson.getString("errorMsg");
	            }
			 else if (responseJson.getBoolean("status") == false) {
			 Log.i("Response Json Failure:", "" + responseJson);
			 errorMessage = responseJson.getString("notice");
			 }
			 } else {
			 errorMessage = getString(R.string.connection_not_available);
			 }
			 } catch (JSONException e) {
			 Log.i("Exception: ", "" + e.getMessage());
			 }

		}
	}
	
	  public void confirmMessageDialog(Context context, String message) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(context);
	        builder.setMessage(message).setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

	            public void onClick(DialogInterface dialog, int id) {

	            }
	        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {


	            }

	        });
	        
	        AlertDialog alert = builder.create();
	        alert.show();
	    }
	
}
