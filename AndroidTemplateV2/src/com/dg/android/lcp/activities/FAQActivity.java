package com.dg.android.lcp.activities;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dg.android.lcp.objects.SessionManager;

public class FAQActivity extends Activity {

    Context context;
    String TAG = "Faq Activity";
    TextView mail1;
    TextView mail2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.faq);
        Log.i(TAG, "Activity called");
        mail1 = (TextView) findViewById(R.id.mail_1);
        mail2 = (TextView) findViewById(R.id.mail_2);
        mail1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {getString(R.string.support_email)});
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
        mail2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {getString( R.string.support_email)});
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
