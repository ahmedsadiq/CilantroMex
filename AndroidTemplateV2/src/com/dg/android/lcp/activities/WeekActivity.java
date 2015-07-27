package com.dg.android.lcp.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dg.android.lcp.objects.ReceiptsObject;
import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.utils.AndroidUtil;
import com.google.gson.Gson;

public class WeekActivity extends Activity {

    Context context;
    String TAG = "Week Activity";
    TableRow claim;
    JSONObject responseJson;
    String errorMessage;
    JSONArray receiptsListArray;
    ReceiptsObject receiptsObject[];

    Gson gson = new Gson();

    private LayoutInflater mInflater;
    // List<ActivityDataObject> locations = new ArrayList<ActivityDataObject>();
    ProgressDialog progressDialog;
    Bundle extras;
    String totalPoints;
    TextView totalPointsNumber;
    DateFormat df2;

    ReceiptsObject locations[];// = new ReceiptsObject()[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.week_view_activity);
        Log.i(TAG, "Activity called");

        extras = getIntent().getExtras();
        totalPoints = extras.getString("totalPoints");
        totalPointsNumber = (TextView) findViewById(R.id.totalPointsNumber);
        totalPointsNumber.setText(totalPoints);
        claim = (TableRow) findViewById(R.id.totalPointsRow);

        new LoadWeekDataAsyncTask().execute();

    }

    private class LoadWeekDataAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);

            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog != null) progressDialog.dismiss();
            if (errorMessage != null && errorMessage.length() > 0 &&errorMessage.equals("401")) {
                AndroidUtil.showMessageDialogWithNewIntent(WeekActivity.this, context, getString(R.string.errorMessage401), LoginSignupActivity.class, "true");
            }
            else if (errorMessage != null && errorMessage.length() > 0) {
                AndroidUtil.showMessageDialog(context, errorMessage);
            } else {
            weekRowAdapter();
            }
        }

        @Override
        protected Void doInBackground(String... arg0) {
            loadActivityData();
            return null;
        }
    }// load week data

    private void loadActivityData() {
        Log.i("requestServerver Method is called", "....................");
        try {
            String url = AndroidUtil.BASE_URL + "/user/activity?auth_token=" + SessionManager.getLoggedInToken(context) + "&appkey="
                    + ApplicationController.APP_ID;
            url += "&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//            String url = getString(R.string.http_url) + "/user/activity?auth_token=" + SessionManager.getLoggedInToken(context) + "&appkey="
//                    + ApplicationController.APP_ID;
//            url += "&locale="+getString(R.string.locale_header_value);
            Log.i("request", "url " + url);
            responseJson = com.dg.android.lcp.utils.ServerRequest.sendRequestWithoAuthToken(url, null);
            if (responseJson.getBoolean("status") == true) {
                Log.i("WeekActivity ", "acitivity list  " + responseJson);
                errorMessage = "";
                receiptsListArray = new JSONArray(responseJson.getString("receipts"));
                locations = gson.fromJson(receiptsListArray.toString(), ReceiptsObject[].class);
            } 
            else if (responseJson.getBoolean("status") == false && !(responseJson.isNull("errorMsg"))) {
                Log.i("Response Json Failure:", "" + responseJson.toString());
                errorMessage=responseJson.getString("errorMsg");
            }
            else if (responseJson.getBoolean("status") == false) {
                Log.i("WeekActivity ", "acitivity list  " + responseJson);
                errorMessage = responseJson.getString("notice");
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void weekRowAdapter() {

        TableLayout table = (TableLayout) findViewById(R.id.helptable);

        mInflater = getLayoutInflater();
        Log.i("size is ++++++++", "" + locations.length);
        for (int i = 0; i < locations.length; i++) {
            final int k = i;
            TableRow tr = (TableRow) mInflater.inflate(R.layout.week_activity_row, null);

            final TextView submitDate = (TextView) tr.findViewById(R.id.day1);
            TextView points = (TextView) tr.findViewById(R.id.day1Status);
            ImageView poinImage = (ImageView) tr.findViewById(R.id.day1Image);

            if (i == locations.length - 1) {
                Log.i("size is ++++++++", "" + locations.length + " " + i);
                Drawable myimage = getResources().getDrawable(R.drawable.activity_bottom);
                tr.setBackgroundDrawable(myimage);
            }

            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date dateObj = curFormater.parse(locations[i].getLast_transaction().getCreated_at());

                SimpleDateFormat postFormater = new SimpleDateFormat("dd-MM-yy");

                String newDateStr = postFormater.format(dateObj);
                submitDate.setText("" + newDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "" + locations[i].getLast_transaction().getStatus());
            if (locations[i].getLast_transaction().getStatus().equalsIgnoreCase("1")){
                points.setText(R.string.Received);
                poinImage.setVisibility(View.VISIBLE);
            }
            else if (locations[i].getLast_transaction().getStatus().equalsIgnoreCase("2")){
                points.setText(R.string.Pending);
                poinImage.setVisibility(View.VISIBLE);
            }
            else if (locations[i].getLast_transaction().getStatus().equalsIgnoreCase("3"))
                points.setText(locations[i].getLast_transaction().getTotal_points_earned());

            else {
                points.setText(R.string.Rejected);
                poinImage.setVisibility(View.VISIBLE);
            }
            poinImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.i("clicked....", "the button clicked :" + locations[k].getLast_transaction().getStatus());
                    if (locations[k].getLast_transaction().getStatus().equalsIgnoreCase("1"))
                        AndroidUtil.showMessageDialog(context,
                                getString(R.string.receipt_receive));
                    else if (locations[k].getLast_transaction().getStatus().equalsIgnoreCase("2"))
                        AndroidUtil.showMessageDialog(context,
                                getString(R.string.receipt_process));
                    else if (locations[k].getLast_transaction().getStatus().equalsIgnoreCase("4"))
                        AndroidUtil.showMessageDialog(context,
                                getString(R.string.receipt_rejected));
                }
            });
            tr.setId(i);
            table.addView(tr);
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
        Intent i = new Intent(this, RewardListActivity.class);
        startActivity(i);
    }

    public void showInfo() {
        Intent i = new Intent(this, InfoActivity.class);
        startActivity(i);
        finish();
    }

    public void showWall() {
        Intent i = new Intent(this, FaceBookTabActivity.class);
        startActivity(i);
        finish();
    }

}