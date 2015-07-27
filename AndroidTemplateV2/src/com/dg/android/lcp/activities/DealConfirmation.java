package com.dg.android.lcp.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dg.android.lcp.objects.SessionManager;

public class DealConfirmation extends Activity {

	Context context;
	String TAG = "Deal Confirmation ";

	Button faq;
	Button rewards;
	TextView myRewards;
	String address;
	String contact;
	JSONObject restaurant;
	String name = "";
	TextView locationAddress;
	TextView locationContact;
	TextView restaurentName;
	TableLayout mapLayout;
	LinearLayout callLayout;
	double latitude;
	double longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.deal_success);
		Log.i(TAG, "Activity called");
		try {
			restaurant = new JSONObject(SessionManager.getRestaurant(context));
			address = restaurant.getString("address");
			name = restaurant.getString("app_display_text");
			contact = restaurant.getString("phone_number");
			latitude = Double.parseDouble(restaurant.getString("latitude"));
			longitude = Double.parseDouble(restaurant.getString("longitude"));

			locationAddress = (TextView) findViewById(R.id.locationAddress);
			locationContact = (TextView) findViewById(R.id.locationContact);
			restaurentName = (TextView) findViewById(R.id.restaurentNameText);
			mapLayout = (TableLayout) findViewById(R.id.addresslayout);
			callLayout = (LinearLayout) findViewById(R.id.phoneLayout);
			
			locationAddress.setText(address);
			locationContact.setText(contact);
			restaurentName.setText(name);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		 callLayout.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {
	                Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + locationContact.getText()));
	                startActivity(dialIntent);
	            }
	        });

	        mapLayout.setOnClickListener(new OnClickListener() {

	            @Override
	            public void onClick(View arg0) {
	                Intent intent = new Intent(context, GoogleMapActivity.class);
	                intent.putExtra("latitude", latitude);
	                intent.putExtra("longitude", longitude);
	                intent.putExtra("address", locationAddress.getText());
	                startActivity(intent);
	            }
	        });
		
	}

	// on back press//////////
	@Override
	public void onBackPressed() {
		Intent i = new Intent(this, LocationListingActivity.class);
		startActivity(i);

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
	}

	public void showWall() {
		Intent i = new Intent(this, FaceBookTabActivity.class);
		startActivity(i);
	}
}