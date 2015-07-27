package com.dg.android.lcp.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dg.android.lcp.objects.SessionManager;

public class LocationDetailActivity extends Activity {

    Context context;
    String TAG = "Location Detail Screen";
    String appId;
    Bundle extras;
    TextView restaurentName, miniDescription, restaurentAddress, restaurentPhoneNo;
    ImageView submit;
    ProgressDialog progressDialog;
    Uri imageUri;
    String message;

    String claimId = "";
    JSONObject restaurant;

    String offerTitle;

    RelativeLayout body;
    RelativeLayout restaurantNameLayout;
    TableLayout mapLayout;
    LinearLayout callLayout;

    double latitude;
    double longitude;
    String restaurentId;
    String restaurantLatitude;
    String restaurantLongitude;

    String offerId = "";
    String offerName;



    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.location_detail_view);
        Log.i(TAG, "Activity called:getRestaurant>" + SessionManager.getRestaurant(context));
        Log.i(TAG, "Activity called:getOffer>" + SessionManager.getOffer(context));

        body = (RelativeLayout) findViewById(R.id.body);
        body.setVisibility(View.GONE);
        
        restaurantNameLayout = (RelativeLayout) findViewById(R.id.restaurentname);
        restaurantNameLayout.setVisibility(View.GONE);
        
        restaurentName = (TextView) findViewById(R.id.restaurentNameText);
        miniDescription = (TextView) findViewById(R.id.miniDescription);
        restaurentAddress = (TextView) findViewById(R.id.locationAddress);
        restaurentPhoneNo = (TextView) findViewById(R.id.locationContact);

        mapLayout = (TableLayout) findViewById(R.id.addresslayout);
        callLayout = (LinearLayout) findViewById(R.id.callLayout);

        new GetLocationDetailsAsyncTask().execute();


    }

    private class GetLocationDetailsAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", getString(R.string.load_loc), true);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            body.setVisibility(View.VISIBLE);
            restaurantNameLayout.setVisibility(View.VISIBLE);
            Log.i("Restaurant Id and offier Id ", restaurentId + "     " + offerId);
        }

        @Override
        protected Void doInBackground(String... params) {
            renderLocationDetails();
            return null;
        }

    }


    /**
     * This method will render the data from the xml op.
     */
    public void renderLocationDetails() {

        try {
            restaurant = new JSONObject(SessionManager.getRestaurant(context));

            restaurentId = restaurant.getString("id");
            restaurentName.setText(restaurant.getString("app_display_text"));
            restaurentAddress.setText(restaurant.getString("address"));
            restaurentPhoneNo.setText(restaurant.getString("phone_number"));

            latitude = Double.parseDouble(restaurant.getString("latitude"));
            longitude = Double.parseDouble(restaurant.getString("longitude"));

            JSONObject offer = new JSONObject(SessionManager.getOffer(context));

            offerId = offer.getString("id");
            offerName = offer.getString("name");

        } catch (JSONException e) {
            Log.i("Exception:", "Exception:" + e.getMessage());
            e.printStackTrace();
        }

        callLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + restaurentPhoneNo.getText()));
                startActivity(dialIntent);
            }
        });

        mapLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(context, GoogleMapActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("address", restaurentAddress.getText());
                startActivity(intent);
            }
        });

        submit = (ImageView) findViewById(R.id.cameraSubmit);
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!SessionManager.isUserLoggedIn(context)) {
                    Intent intent = new Intent(context, LoginSignupActivity.class);
                    intent.putExtra("restaurantId", restaurentId);
                    intent.putExtra("offerId", offerId);
                    SessionManager.setNextActivity(context, "1");
                    startActivity(intent);
                    return;
                } else {
                    Intent intent = new Intent(context, CustomCameraActivity.class);
//                    intent.putExtra("restaurantId", restaurentId);
//                    intent.putExtra("offerId", "306860403");
//                    intent.putExtra("address", "Address hardcoded.");
//                    intent.putExtra("phone", "1234567890");
//                    intent.putExtra("name", "restaurant Name");

                    // intent.putExtra("offerId", offerId);
                    // intent.putExtra("address", restaurantAddress);
                    // intent.putExtra("phone", restaurantPhoneNo);
                    // intent.putExtra("name", restaurantName);
                    startActivity(intent);

                }

            }
        });

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
    }// //////////////////// menu
    
    

}
