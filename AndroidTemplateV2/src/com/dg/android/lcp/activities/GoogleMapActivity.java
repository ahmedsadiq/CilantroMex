package com.dg.android.lcp.activities;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.utils.MapMarker;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class GoogleMapActivity extends MapActivity {

    GeoPoint eventPoint, centerPoint, centrePintUser;
    Context context;
    String TAG = "Google map";
    MapView mapView;
    GeoPoint point;
    Projection projection;
    double latitude;
    double longitude;
    String cityName;
    String address;
    String zipCode;
    Bundle extras;
    Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.google_maps);
        Log.i(TAG, "Activity called");
        extras = getIntent().getExtras();
        latitude = extras.getDouble("latitude");
        longitude = extras.getDouble("longitude");

        address = extras.getString("address");
        Log.i("information is ----------", "name" + cityName + " addrsess" + address + "zip code" + zipCode);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setSatellite(false);
        mapView.setBuiltInZoomControls(true);
        Log.i("Latitu-----------", "Lat is " + latitude + "   long is   " + longitude);
        eventPoint = new GeoPoint((int) (latitude), (int) (longitude));
        centerPoint = new GeoPoint((int) (latitude * 1e6), (int) (longitude * 1e6));
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.map_default_marker);
        MapMarker mapMarker = new MapMarker(drawable, this, LAYOUT_INFLATER_SERVICE, findViewById(R.id.layout_root), address);

        OverlayItem overlayitem = new OverlayItem(centerPoint, "Map", "test");

        mapMarker.addOverlay(overlayitem);
        mapOverlays.add(mapMarker);

        mapView.getController().setCenter(centerPoint);
        mapView.getController().setZoom(17);
        projection = mapView.getProjection();

        mapView.invalidate();

    }

    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            myLocation = location;
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
