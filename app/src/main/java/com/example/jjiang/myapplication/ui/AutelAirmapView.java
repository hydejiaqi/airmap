package com.example.jjiang.myapplication.ui;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.airmap.airmapsdk.ui.views.AirMapMapView;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;


import java.util.ArrayList;

/**
 * Created by jjiang on 4/19/2018.
 */

public class AutelAirmapView extends AirMapMapView{

    private ArrayList<MarkerOptions> markers;


    @Override
    public void init(Configuration configuration) {
        super.init(configuration);
        markers = new ArrayList<MarkerOptions>();
    }

    @Override
    public void onMapClick(@NonNull LatLng point) {
        super.onMapClick(point);


    }

    public AutelAirmapView(@NonNull Context context) {
        super(context);
    }

    public AutelAirmapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutelAirmapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




}
