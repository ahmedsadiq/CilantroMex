package com.dg.android.lcp.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dg.android.lcp.objects.LocationDataObject1;
import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.utils.AndroidUtil;
import com.dg.android.lcp.utils.ServerRequest;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;


public class LocationListingActivity extends Activity {
	
	
	public static String resId = "";
	String TAG = "LocationListingActivity";
    Context context;
    RelativeLayout header;
    ScrollView scroll;
    RelativeLayout splash;
    private LayoutInflater mInflater;
    ProgressDialog progressDialog;
    private static boolean firstStart = true;
    ScrollView scr;
    String errorMessage;
    Location myLocation;
    boolean isActivity = false;
    Gson gson = new Gson();
    JSONObject responseJson;
    Bundle extras;
    JSONArray restaurantArray;
    LocationDataObject1 locations[];



    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.location_list_view);
//        new GCMRegisterAsyncTask().execute();
        scr = (ScrollView) findViewById(R.id.scroll);
        scr.setVisibility(View.VISIBLE);

        mInflater = getLayoutInflater();
        LocationManager locManager;
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 500.0f, locationListener);
        myLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (!firstStart) {
            View v = findViewById(R.id.splashScreen);
            v.setVisibility(View.GONE);
            v = findViewById(R.id.locationListLayout);
            v.setVisibility(View.VISIBLE);
        }else{
            if (!SessionManager.getAppStart(context)) {
              Log.i("appStarted", "appStarted TRUE.....");
              new GetAppKeyStatus().execute();
          } else {
              Log.i("appStarted", "appStarted FALSE.....");
          }
        }
        new LoadLocationsAsyncTask().execute();
    }

    private class LoadLocationsAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            errorMessage = "";
            if (!firstStart) {

                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);

                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
            } else {
                firstStart = false;
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
           
            SessionManager.setBackpress(false);
            isActivity = true;
            try {
                if (progressDialog != null) progressDialog.dismiss();
                View v = findViewById(R.id.splashScreen);
                v.setVisibility(View.GONE);
                v = findViewById(R.id.locationListLayout);
                v.setVisibility(View.VISIBLE);
                // To scroll always from top
                scr.fullScroll(ScrollView.FOCUS_UP);
                scr.pageScroll(ScrollView.FOCUS_UP);
                scr.pageScroll(ScrollView.FOCUS_UP);
                scr.pageScroll(ScrollView.FOCUS_UP);
                scr.pageScroll(ScrollView.FOCUS_UP);

                if (errorMessage != null && errorMessage.length() > 0) {
                    AndroidUtil.showMessageDialog(context, errorMessage);
                    errorMessage = "";
                } else {

                    restaurantArray = new JSONArray(responseJson.getString("restaurants"));

                    locations = gson.fromJson(restaurantArray.toString(), LocationDataObject1[].class);
                    Log.i("Address ", locations[0].getName() + "................. ");

                    locationsRowAdapter();
                }

            }

            catch (JSONException e) {
                e.printStackTrace();

            }

        }

        @Override
        protected Void doInBackground(String... params) {
            requestServer();
            if(SessionManager.getResId(context)==null){
            	Log.i(TAG,"Registration Id = nul");
             gcmGegistration();
            }
            else{
            	Log.i(TAG,"Registration id="+SessionManager.getResId(context));
            }
           return null;
        }

    }

    private void requestServer() {
        Log.i("requestServerver Method is called", "....................");
        try {
            if (myLocation == null) {
                errorMessage = getString(R.string.no_location_error_message);
                return;
            }

//            String url = AndroidUtil.BASE_URL + "/offers/nearby?lat=" + myLocation.getLatitude() + "&lng=" + myLocation.getLongitude()
//                    + "&distance=5000&appkey=" + ApplicationController.APP_ID;
            String url = AndroidUtil.BASE_URL + "/restaurants/nearby?lat=" + "0.0" + "&lng=" + "0.0"
                  + "&distance=0&appkey=" + ApplicationController.APP_ID;
            url += "&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//            url += "&locale="+getString(R.string.locale_header_value);
            Log.i(TAG,url);
            String param = null;

            Log.i("request", "url " + url + "  param " + param);
            responseJson = ServerRequest.sendRequest(url, param);
            if (responseJson != null) {
                if (responseJson.getBoolean("status") == true) {
                    // if successfully get the info of user from server
                    Log.i("FeaturedEventActivity ", "User detail  " + responseJson);
                    errorMessage = "";
                } else if (responseJson.getBoolean("status") == false) {
                    errorMessage = responseJson.getString("notice");
                }
            } else {
                errorMessage = getString(R.string.connection_not_available);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void locationsRowAdapter() {
        TableLayout table = (TableLayout) findViewById(R.id.restaurent_detail_table);
        table.removeAllViews();

        if (locations.length == 0) {
            AndroidUtil.showMessageDialog(context, getString(R.string.sorry));
            return;
        }

        int rowNum = 0;

        for (int i = 0; i < locations.length; i++) {
            for (int j = 0; j < locations[i].getAvailable_offers().length; j++) {


                final TableRow tr = (TableRow) mInflater.inflate(R.layout.location_row, null);

                final TextView offerId = (TextView) tr.findViewById(R.id.offerId);
                TextView restaurentName = (TextView) tr.findViewById(R.id.restaurentName);
                ImageView points = (ImageView) tr.findViewById(R.id.points);


                offerId.setText(j + "");
                restaurentName.setText(locations[i].getApp_display_text());

                locations[i].getAvailable_offers()[j].getId();

                rowNum = rowNum + 1;

                Log.i("rowNum", "" + rowNum);
                // tr.setId((rowNum-1));
                tr.setId(i);
                table.addView(tr);

                // tr.setId(i);
                // table.addView(tr);
                tr.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        SessionManager.setRestaurantAndOffer(context, gson.toJson(locations[v.getId()]).toString(),
                                gson.toJson(locations[v.getId()].getAvailable_offers()[Integer.parseInt(offerId.getText().toString())]).toString());

                        Intent intent = new Intent(context, LocationDetailActivity.class);

                        startActivity(intent);
                    }
                });

            }

        }
    }


    private final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private class GetAppKeyStatus extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            errorMessage = "";

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {
                if (responseJson != null) {
                    if (responseJson.getBoolean("status") == true) {
                        Log.i("appkey ", "apkey is OK");
                    } else if (responseJson.getBoolean("status") == false) {
                        errorMessage = responseJson.getString("notice");
                        Log.i("STATUS ", "STATUS :"+errorMessage);
                        new AlertDialog.Builder(LocationListingActivity.this).setTitle("")
                        .setMessage(errorMessage)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                SessionManager.setAppStart(context, true);
                            }
                        }).setNegativeButton(R.string.remind_me_later, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                } else {
                    errorMessage = getString(R.string.connection_not_available);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            requestAppkey();
            return null;
        }
    }

    private void requestAppkey() {
        Log.i("requestServerver Method is called", "....................");
        String url = AndroidUtil.BASE_URL+"/version/latest?appkey="+ApplicationController.APP_ID;
        url += "&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//        String url = getString(R.string.http_url)+"/version/latest?appkey="+ApplicationController.APP_ID;
//        url += "&locale="+getString(R.string.locale_header_value);
        Log.i("request", "url " + url + "");
        responseJson = ServerRequest.sendRequest(url, null);
    }

    /**
     * update user current location.s
     * 
     * @param location
     */
    private void updateWithNewLocation(Location location) {
        if (location != null) {
            myLocation = location;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("Resume----", "" + SessionManager.getbackpress());
        SessionManager.setBackpress(true);
//        unregisterReceiver(mHandleMessageReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Resume--------", "On resume---");
        if (!SessionManager.getbackpress() && isActivity) new LoadLocationsAsyncTask().execute();
       
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mHandleMessageReceiver);
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

//	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String newMessage = intent.getExtras().getString(com.dg.android.lcp.activities.CommonUtilities.EXTRA_MESSAGE);
////			AndroidUtil.showMessageDialog(context, newMessage);
//		}
//	};
	
	public void gcmGegistration(){
		GCMRegistrar.checkDevice(context);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(context);
//		registerReceiver(mHandleMessageReceiver,new IntentFilter(com.dg.android.lcp.activities.CommonUtilities.DISPLAY_MESSAGE_ACTION));
		
		resId = GCMRegistrar.getRegistrationId(context);
		if (resId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(context, "389484218706");
			resId = GCMRegistrar.getRegistrationId(context);
		}
		else{
			 SessionManager.setResId(context, resId);
		}
	}
 
	
}
