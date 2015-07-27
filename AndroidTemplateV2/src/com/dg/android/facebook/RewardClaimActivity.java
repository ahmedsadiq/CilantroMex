//package com.dg.android.facebook;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.dg.android.lcp.activities.ApplicationController;
//import com.dg.android.lcp.activities.FaceBookTabActivity;
//import com.dg.android.lcp.activities.InfoActivity;
//import com.dg.android.lcp.activities.LocationListingActivity;
//import com.dg.android.lcp.activities.LoginSignupActivity;
//import com.dg.android.lcp.activities.R;
//import com.dg.android.lcp.activities.RewardListActivity;
//import com.dg.android.lcp.activities.R.id;
//import com.dg.android.lcp.activities.R.layout;
//import com.dg.android.lcp.activities.R.menu;
//import com.dg.android.lcp.activities.R.string;
//import com.dg.android.lcp.objects.RestaurantAddressObject;
//import com.dg.android.lcp.objects.SessionManager;
//import com.dg.android.lcp.utils.AndroidUtil;
//import com.dg.android.lcp.utils.ServerRequest;
//import com.google.gson.Gson;
//
//public class RewardClaimActivity extends Activity {
//
//    Context context;
//    String TAG = "Reward Claim Activity";
//    Button rewardclaim;
//    Bundle extras;
//    String rewardname;
//    public static String restaurantadd;
//    public static int restaurantId;
//    TextView rewardName;
//    TextView restaurantAddress;
//
//    ProgressDialog progressDialog;
//    String message;
//    String rewardId;
//    String chainName;
//    String posCode;
//    String pointsClaim;
//    String pos = "46";
//    Button rewardusedButton;
//    double latitude;
//    double longitude;
//
//    String errorMessage;
//    Gson gson = new Gson();
//    JSONObject responseJson;
//    JSONArray restaurantAddressArray;
//    RestaurantAddressObject restaurantAddressObjects[];
//
//    Location myLocation;
//    LocationManager locManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.reward_claim);
//        context = this;
//
//        new GetRestaurantAddressAsyncTask().execute();
//
//        Log.i(TAG, "Activity called");
//        extras = getIntent().getExtras();
//        rewardname = extras.getString("rewardName");
//        rewardId = extras.getString("rewardId");
//        posCode = extras.getString("posCode");
//        pointsClaim = extras.getString("pointsClaim");
//        chainName = extras.getString("chainName");
//
//        rewardName = (TextView) findViewById(R.id.freeoffer);
//        rewardName.setText(rewardname);
//
//        restaurantAddress = (TextView) findViewById(R.id.restaurantAddress);
//        rewardclaim = (Button) findViewById(R.id.markButton);
//        rewardusedButton = (Button) findViewById(R.id.rewardusedButton);
//
//        rewardclaim.setText(getString(R.string.staff_use_code) + posCode);
//        rewardusedButton.setText(getString(R.string.staff_use_code) + posCode);
//
//        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 500.0f, locationListener);
//        myLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//        rewardclaim.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                RewardClaimCall();
//            }
//        });
//
//    }
//
//    // this Class is used for get address
//
//    private class GetRestaurantAddressAsyncTask extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            errorMessage = "";
//            progressDialog = ProgressDialog.show(context, "",getString(R.string.reward_list),true);
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            progressDialog.dismiss();
//            try {
//            	if (errorMessage != null && errorMessage.length() > 0 &&errorMessage.equals("401")) {
//                    AndroidUtil.showMessageDialogWithNewIntent(RewardClaimActivity.this, context, getString(R.string.errorMessage401), LoginSignupActivity.class, "true");
//                }
//          	  else if (errorMessage != null && errorMessage.length() > 0) {
//                    AndroidUtil.showMessageDialog(context, errorMessage);
//                } else {
//
//                    restaurantAddressArray = new JSONArray(responseJson.getString("restaurants"));
//                    restaurantAddressObjects = gson.fromJson(restaurantAddressArray.toString(), RestaurantAddressObject[].class);
//
//                    restaurantadd = restaurantAddressObjects[0].getAddress();
//                    restaurantId = Integer.parseInt(restaurantAddressObjects[0].getId());
//                    Log.i("Address", restaurantadd);
//                    restaurantAddress.setText(restaurantadd);
//                }
//
//            }
//
//            catch (JSONException e) {
//                e.printStackTrace();
//
//            }
//
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//            requestServerForAddress();
//            return null;
//        }
//    }
//
//    private void requestServerForAddress() {
//        Log.i("requestServerver1 Method is called", "....................");
//        try {
//            String url = getString(R.string.http_url) + "/rewards/locate?auth_token=" + SessionManager.getLoggedInToken(context) + "&appkey="
//                    + ApplicationController.APP_ID;
//            url += "&locale="+getString(R.string.locale_header_value);
//            Log.i("request", "url " + url);
//            responseJson = ServerRequest.sendRequestWithoAuthToken(url, null);
//            if (responseJson.getBoolean("status") == true) {
//                // if successfully get the info of user from server
//                Log.i("RewardListActivity ", "reward list  " + responseJson);
//                errorMessage = "";
//            } 
//            else if (responseJson.getBoolean("status") == false&&!(responseJson.isNull("errorMsg"))) {
//                Log.i("Response Json Failure:", "" + responseJson.toString());
//                errorMessage=responseJson.getString("errorMsg");
//            }
//            else if (responseJson.getBoolean("status") == false) {
//                errorMessage = responseJson.getString("message");
//            }
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }
//
//    // Reward claim confirmation method
//
//    private void RewardClaimCall() {
//        latitude = myLocation.getLatitude();
//        longitude = myLocation.getLongitude();
//        Log.i("latitude ", latitude + "");
//        Log.i("longitude", longitude + "");
//
//
//        // param="appkey="
//        // +ApplicationController.APP_ID+"&auth_token="+SessionManager.getLoggedInToken(context)+"&reward_id="+rewardId+"&lat=40.734362&lng=-73.9924351&location="+restaurantId;
//        // param = "appkey=" + ApplicationController.APP_ID + "&auth_token=" + SessionManager.getLoggedInToken(context) + "&reward_id=" + rewardId
//        // + "&lat=" + latitude + "&lng=" + longitude + "&location=" + restaurantId;
//        new AlertDialog.Builder(RewardClaimActivity.this).setTitle("").setMessage(getString(R.string.reward_claim_popup))
//                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        new ClaimRewardAsyncTask().execute();
//                    }
//                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        Intent i = new Intent(context, RewardListActivity.class);
//                        startActivity(i);
//                        finish();
//                    }
//                }).show();
//    }
//
//
//
//
//    // / this Class is for use claim rewards
//
//    private class ClaimRewardAsyncTask extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            errorMessage = "";
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.setCancelable(false);
//            progressDialog.setMessage(getString(R.string.loading));
//            progressDialog.show();
//
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            progressDialog.dismiss();
//            try {
//                if (errorMessage != null && errorMessage.length() > 0)
//                    AndroidUtil.showMessageDialog(context, errorMessage);
//
//
//                else if (responseJson.getBoolean("status") == true) {
//                    AndroidUtil.showMessageDialog(context, responseJson.getString("notice"));
//                    rewardusedButton.setVisibility(View.VISIBLE);
//                    rewardclaim.setVisibility(View.GONE);
//                } else if (responseJson.getBoolean("status") == false) {
//
//                    AndroidUtil.showMessageDialog(context, message);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//
//
//        @Override
//        protected Void doInBackground(String... params) {
//            requestServerForClaim();
//            return null;
//        }
//
//    }
//
//    private void requestServerForClaim() {
//        Log.i("requestServerver Method is called", "....................");
//        try {
//            String url = getString(R.string.http_url)+"/rewards/claim?";
//            String param = "appkey=" + ApplicationController.APP_ID + "&auth_token=" + SessionManager.getLoggedInToken(context) + "&reward_id=" + rewardId
//            + "&lat=" + latitude + 
//            "&lng=" + longitude + 
//            "&location=" + restaurantId + 
//            "&locale="+getString(R.string.locale_header_value);
//            
//            Log.i("request", "url " + url + "  param " + param);
//            responseJson = ServerRequest.sendPostRequest(url, param);
//            if (responseJson.getBoolean("status") == true) {
//                Log.i("RewardClaimActivity ", "reward claim" + responseJson);
//                errorMessage = "";
//
//            } else if (responseJson.getBoolean("status") == false) {
//                errorMessage = responseJson.getString("notice");
//            }
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        Intent i = new Intent(this, RewardListActivity.class);
//        startActivity(i);
//        finish();
//    }
//
//    /**
//     * Menu items are created here
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.aroma_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menulocations:
//                showLocations();
//                return true;
//            case R.id.menurewards:
//                showRewards();
//                return true;
//            case R.id.menuwall:
//                showWall();
//                return true;
//            case R.id.menuinfo:
//                showInfo();
//                return true;
//            default:
//                return false;
//        }
//    }
//
//    public void showLocations() {
//        Intent i = new Intent(this, LocationListingActivity.class);
//        startActivity(i);
//    }
//
//    public void showRewards() {
//        Intent i = new Intent(this, RewardListActivity.class);
//        startActivity(i);
//    }
//
//    public void showInfo() {
//        Intent i = new Intent(this, InfoActivity.class);
//        startActivity(i);
//        finish();
//    }
//
//    public void showWall() {
//        Intent i = new Intent(this, FaceBookTabActivity.class);
//        startActivity(i);
//        finish();
//    }
//
//
//
//
//
//
//    private final LocationListener locationListener = new LocationListener() {
//
//        public void onLocationChanged(Location location) {
//            updateWithNewLocation(location);
//        }
//
//        public void onProviderDisabled(String provider) {
//            updateWithNewLocation(null);
//        }
//
//        public void onProviderEnabled(String provider) {
//        }
//
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//        }
//    };
//
//    private void updateWithNewLocation(Location location) {
//
//        if (location != null) {
//            myLocation = location;
//        }
//
//    }
//
//
//
//
//
//
//
//
//}