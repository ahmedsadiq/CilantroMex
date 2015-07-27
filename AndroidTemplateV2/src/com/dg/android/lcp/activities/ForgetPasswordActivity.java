    package com.dg.android.lcp.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.utils.AndroidUtil;
import com.dg.android.lcp.utils.ServerRequest;


public class ForgetPasswordActivity extends Activity {

    Context context;
    String TAG = "Forget Password ";
    Button send;
    EditText email;
    ProgressDialog progressDialog;
    String errorMessage = "";
    JSONObject responseJson;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.forget_password);
        Log.i(TAG, "Activity called");
        email = (EditText) findViewById(R.id.email);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.i(TAG, "Sending email to retrieve password");
                new SendingEmailAsyncTask().execute();
            }
        });

    }

    // email form //////////
    private class SendingEmailAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
             InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
             imm.hideSoftInputFromWindow(email.getWindowToken(), 0);
             errorMessage="";
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.wait));
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            
            try {
                if (responseJson.getBoolean("status") == true) {
                    errorMessage = responseJson.getString("notice");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(errorMessage).setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                            return;
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    
                } else if (responseJson.getBoolean("status") == false) {
                    Log.i("Response Json Failure:", "" + responseJson);
                    errorMessage = responseJson.getString("notice");
                    AndroidUtil.showMessageDialog(context, errorMessage);
                }
            } catch (JSONException e) {
                Log.i("Exception: ", "" + e.getMessage());
            }

            
            if (errorMessage != null && errorMessage.length() > 0) 

            if (errorMessage.contains(getString(R.string.password_emailed))) {
                finish();
            }

        }

        @Override
        protected Void doInBackground(String... params) {
            sendRequest();
            return null;
        }

    }

    private void sendRequest() {
        String url = AndroidUtil.BASE_URL + "/user/forgot_password";
        String params = "appkey=" + ApplicationController.APP_ID + 
        		"&email=" + email.getText().toString() + 
        		"&register_type=1" +
        		"&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//       
//        String url = getString(R.string.http_url) + "/user/forgot_password";
//        String params = "appkey=" + ApplicationController.APP_ID + 
//        		"&email=" + email.getText().toString() + 
//        		"&register_type=1" +
//        		"&locale="+getString(R.string.locale_header_value);
        Log.i("request", "url " + url + "  params " + params);
        responseJson = ServerRequest.sendPostRequest(url, params);
        
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
}
