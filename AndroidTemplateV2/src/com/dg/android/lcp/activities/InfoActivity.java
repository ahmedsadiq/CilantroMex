package com.dg.android.lcp.activities;


import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableRow;
import android.widget.TextView;

import com.dg.android.facebook.SessionStore;
import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.utils.AndroidUtil;
import com.dg.android.lcp.utils.ServerRequest;

public class InfoActivity extends Activity {

    Context context;
    String TAG = "Help List";
    TableRow aboutvivo;
    TableRow getRewards;

    TableRow contactUs;
    TableRow faqRow;
    TableRow logOut;
    TableRow updatePassword;
    TableRow promoCodeRow;
    TextView loginText;
    ProgressDialog progressDialog;
    JSONObject responseJson;
    String errorMessage, message;
	private TableRow menuvivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.info_activity);
        Log.i(TAG, "Activity called");
        menuvivo = (TableRow) findViewById(R.id.menuVivoRow);
        aboutvivo = (TableRow) findViewById(R.id.aboutVivoRow);
        getRewards = (TableRow) findViewById(R.id.getRewardsRow);
        logOut = (TableRow) findViewById(R.id.logoutRow);
        contactUs = (TableRow) findViewById(R.id.contactUsRow);
        updatePassword = (TableRow) findViewById(R.id.updatePasswordRow);

        promoCodeRow = (TableRow) findViewById(R.id.promoCodeRow);
        faqRow = (TableRow) findViewById(R.id.faqRow);
        loginText = (TextView) findViewById(R.id.logout);
        if (!SessionManager.isUserLoggedIn(context)) {
            loginText.setText(R.string.log_in);
        }

        menuvivo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, TermsOfUseActivity.class);
				intent.putExtra("MENUKEY", "menu");
                startActivity(intent);
            }
        });
        aboutvivo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, AboutActivity.class);
                startActivity(intent);

            }
        });

        getRewards.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, HowItWorksActivity.class);
                startActivity(intent);

            }
        });

        faqRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, FAQActivity.class);
                startActivity(intent);

            }
        });

        promoCodeRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
            	if(SessionManager.isUserLoggedIn(context))
            	{
                Intent intent = new Intent(context, PromoCodeActivity.class);
                startActivity(intent);
            	}
            	else
            	{
            		 Intent intent = new Intent(context, LoginSignupActivity.class);
            		 SessionManager.setNextActivity(context, "10");
                     startActivity(intent);
            	}
            }
        });
        contactUs.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { getString(R.string.support_email)});
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                final PackageManager pm = getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
                ResolveInfo best = null;
                for (final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
                if (best != null) intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                startActivity(intent);

            }
        });

        logOut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!SessionManager.isUserLoggedIn(context)) {
                    Intent intent = new Intent(context, LoginSignupActivity.class);
                    SessionManager.setNextActivity(context, "4");
                    startActivity(intent);
                } else {
                    new LogoutUserAsyncTask().execute();
                }
            }
        });


        updatePassword.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!SessionManager.isUserLoggedIn(context)) {
                    Intent intent = new Intent(context, LoginSignupActivity.class);
                    SessionManager.setNextActivity(context, "7");
                    startActivity(intent);
                }

                else {
                    Intent intent = new Intent(context, UpdatePasswordActivity.class);
                    startActivity(intent);
                }
            }
        });


    }

    private class LogoutUserAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", getString(R.string.logout_progress_dialog), true);
            errorMessage="";
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (errorMessage != null && errorMessage.length() > 0 &&errorMessage.equals("401")) {
                AndroidUtil.showMessageDialogWithNewIntent(InfoActivity.this, context, getString(R.string.errorMessage401), LoginSignupActivity.class, "true");
            }
            else if (errorMessage != null && errorMessage.length() > 0) {
                AndroidUtil.showMessageDialog(context, errorMessage);
            } else {
                loginText.setText(R.string.log_in);
                AndroidUtil.showMessageDialog(context, message);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            doLogout();
            return null;
        }

    }

    private boolean doLogout() {
        String url = AndroidUtil.BASE_URL_HTTPS + "/user/logout?auth_token=" + SessionManager.getLoggedInToken(context)+"&appkey="+ApplicationController.APP_ID;
        url += "&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
        
//        String url = getString(R.string.https_url) + "/user/logout?auth_token=" + SessionManager.getLoggedInToken(context)+"&appkey="+ApplicationController.APP_ID;
//        url += "&locale="+getString(R.string.locale_header_value);
        Log.i("request", "url " + url + "");
        errorMessage = "";
        message = "";
        try {
            responseJson = ServerRequest.sendRequestWithoAuthToken(url, null);
            if(responseJson!=null){
            if (responseJson.getBoolean("status") == true) {
                // if successfully get the info of user from server
            	String gcmRegId = SessionManager.getResId(context);
            	SessionStore.clear(context);
                SessionManager.logout(context);
                SessionManager.setResId(context, gcmRegId);
                errorMessage = "";
                message=responseJson.getString("notice");
            } 
            
            else if (responseJson.getBoolean("status") == false&&!(responseJson.isNull("errorMsg"))) {
                Log.i("Response Json Failure:", "" + responseJson.toString());
                errorMessage=responseJson.getString("errorMsg");
            }
            
            
            else if (responseJson.getBoolean("status") == false) {
                Log.i("Response Json Failure:", "" + responseJson.toString());
                errorMessage=responseJson.getString("notice");
            }
            }
            else{
                errorMessage=getString(R.string.connection_not_available);
            }
        } catch (JSONException e) {
            Log.i("Exception: ", "" + e.getMessage());
        }
        return false;

    }

    /**
     * Menu items are created here
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.aroma_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menulocations:
                showLocations();
                return true;
            case R.id.menurewards:
                showRewards();
                return true;
            case R.id.menuwall:
                showWall();
                return true;
            case R.id.menuinfo:
                showInfo();
                return true;
            default:
                return false;
        }
    }

    public void showLocations() {
        Intent i = new Intent(this, LocationListingActivity.class);
        startActivity(i);
    }

    public void showRewards() {
        if (!SessionManager.isUserLoggedIn(this)) {
            AndroidUtil.showToastMessage(this, getString(R.string.user_not_logged_in));
            Intent i = new Intent(this, LoginSignupActivity.class);
            SessionManager.setNextActivity(context, "2");
            startActivity(i);
            return;
        }
        Intent i = new Intent(this, RewardListActivity.class);
        startActivity(i);
    }

    public void showInfo() {
        Intent i = new Intent(this, InfoActivity.class);
        startActivity(i);
    }

    public void showWall() {
        Intent i = new Intent(this, FaceBookTabActivity.class);
        startActivity(i);
    }
    @Override
    protected void onResume() {
    	super.onResume();
    	Log.i(TAG,"onResume()");
    	if(SessionManager.isUserLoggedIn(context)){
    		 loginText.setText(R.string.log_out);
    		
    	}
    	else{
    		 loginText.setText(R.string.log_in);
    	}
    }
}
