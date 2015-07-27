package com.dg.android.lcp.utils;


import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.dg.android.lcp.activities.R;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapMarker extends ItemizedOverlay {

    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Context mContext;
    private String inflaterService;
    private View viewLayout;
    String address;

    public MapMarker(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
    }


    public MapMarker(Drawable defaultMarker, Context context, String inflaterService, View viewLayout, String address) {
        this(defaultMarker);
        this.mContext = context;
        this.inflaterService = inflaterService;
        this.viewLayout = viewLayout;
        this.address = address;
    }


    public void addOverlay(OverlayItem item) {
        mOverlays.add(item);
        populate();

    }


    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    @Override
    protected boolean onTap(int index) {
        final OverlayItem item = mOverlays.get(index);
        Log.i("text click-------", "click----");
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(inflaterService);
        View mapDialoglayout = inflater.inflate(R.layout.map_dialog_layout, (ViewGroup) viewLayout);
        TextView titleText = (TextView) mapDialoglayout.findViewById(R.id.dialogTitle);
        titleText.setText("Aroma");
        TextView addresstext = (TextView) mapDialoglayout.findViewById(R.id.addressName);
        addresstext.setText(address);

        final Dialog alertDialog = new Dialog(mContext, R.style.MapDialogTheme);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(mapDialoglayout);
        alertDialog.show();

        return true;

    }
}