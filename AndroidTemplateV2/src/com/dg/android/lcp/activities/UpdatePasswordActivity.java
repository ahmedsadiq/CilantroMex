package com.dg.android.lcp.activities;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.utils.AndroidUtil;

public class UpdatePasswordActivity extends Activity {

    Context context;
    String TAG = "Forget Password ";
    Button submitButton;

    EditText oldPassword;
    EditText newPassword;
    EditText repeateNewPassword;

    String oldPasswordStr;
    String newPasswordStr;
    String repeateNewPasswordStr;

    ProgressDialog progressDialog;
    String errorMessage = "";
    String url;
    String param;

    JSONObject responseJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.update_password);
        Log.i(TAG, "Activity called");

        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        repeateNewPassword = (EditText) findViewById(R.id.repeateNewPassword);

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.i(TAG, "Sending Updated password");
                oldPasswordStr = oldPassword.getText().toString();
                newPasswordStr = newPassword.getText().toString();
                repeateNewPasswordStr = repeateNewPassword.getText().toString();

                url = AndroidUtil.BASE_URL+"/user/update_password?";
                param = "appkey=" + ApplicationController.APP_ID + "&auth_token=" + SessionManager.getLoggedInToken(context) + "&password="
                        + newPasswordStr + "&current_password=" + oldPasswordStr + "&password_confirmation=" + repeateNewPasswordStr;
                param += "&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//                
//
//                url = getString(R.string.http_url)+"/user/update_password?";
//                param = "appkey=" + ApplicationController.APP_ID + "&auth_token=" + SessionManager.getLoggedInToken(context) + "&password="
//                        + newPasswordStr + "&current_password=" + oldPasswordStr + "&password_confirmation=" + repeateNewPasswordStr;
//                param += "&locale="+getString(R.string.locale_header_value);
                new UpdatePasswordAsyncTask(url, param).execute();
            }
        });

    }

    // update password class //////////
    private class UpdatePasswordAsyncTask extends AsyncTask<String, Void, Void> {

        String url;
        String param;

        UpdatePasswordAsyncTask(String url, String param) {
            this.url = url;
            this.param = param;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            errorMessage="";
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.update_process));
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
          	if (errorMessage != null && errorMessage.length() > 0 &&errorMessage.equals("401")) {
                AndroidUtil.showMessageDialogWithNewIntent(UpdatePasswordActivity.this, context, getString(R.string.errorMessage401), LoginSignupActivity.class, "true");
            }
          	else if (errorMessage != null && errorMessage.length() > 0) {
            	AndroidUtil.showMessageDialog(context, errorMessage);
            }
            oldPassword.setText("");
            newPassword.setText("");
            repeateNewPassword.setText("");

            /*
             * if (errorMessage .contains("Password has been sent your your email. You will receive it shortly.")) { finish(); }
             */

        }

        @Override
        protected Void doInBackground(String... params) {
            requestServer();
            return null;
        }

        private void requestServer() {
            Log.i("requestServerver Method is called", "....................");
            try {

                Log.i("request", "url " + url + "  param " + param);
                responseJson = com.dg.android.lcp.utils.ServerRequest.sendPostRequestWithoAuthToken(url, param);
                if (responseJson.getBoolean("status") == true) {
                    // if successfully get the info of user from server
                    Log.i("UpdatedPasswordActivity ", "true" + responseJson);
                    errorMessage = responseJson.getString("notice");


                } 
                else if (responseJson.getBoolean("status") == false&&!(responseJson.isNull("errorMsg"))) {
                    Log.i("Response Json Failure:", "" + responseJson.toString());
                    errorMessage=responseJson.getString("errorMsg");
                }
                else if (responseJson.getBoolean("status") == false) {
                    errorMessage = responseJson.getString("notice");
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

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
