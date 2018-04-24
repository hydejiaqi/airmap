package com.example.jjiang.myapplication.ui;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Toast;

import com.airmap.airmapsdk.AirMapException;
import com.airmap.airmapsdk.AirMapLog;
import com.airmap.airmapsdk.Analytics;
import com.airmap.airmapsdk.controllers.MapDataController;
import com.airmap.airmapsdk.controllers.MapStyleController;
import com.airmap.airmapsdk.models.airspace.AirMapAirspace;
import com.airmap.airmapsdk.networking.callbacks.AirMapCallback;
import com.airmap.airmapsdk.networking.services.AirspaceService;
import com.airmap.airmapsdk.ui.views.AirMapMapView;
import com.airmap.airmapsdk.util.Utils;
import com.example.jjiang.myapplication.R;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjiang on 4/19/2018.
 */

public class AutelAirmapView extends MapView implements MapboxMap.OnMapLongClickListener, MapboxMap.OnMapClickListener, MapView.OnMapChangedListener{

    private ArrayList<MarkerOptions> markers;
    private MapboxMap map;
    private MapDataController mapDataController;
    private MapStyleController mapStyleController;


    public AutelAirmapView(@NonNull Context context) {
        super(context);
    }

    public AutelAirmapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutelAirmapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onMapLongClick(@NonNull LatLng point) {



    }

    @Override
    public void onMapClick(@NonNull LatLng point) {

    }

    @Override
    public void onMapChanged(int change) {
        switch (change){

            case MapView.DID_FAIL_LOADING_MAP: {
               /* // Devices without internet connection will not be able to load the mapbox map
                //TODO: add more sophisticated check (like actually check style url for 200)
                if (!Utils.isNetworkConnected(getContext())) {
                    for (AirMapMapView.OnMapLoadListener mapLoadListener : mapLoadListeners) {
                        mapLoadListener.onMapFailed(AirMapMapView.MapFailure.NETWORK_CONNECTION_FAILURE);
                    }

                    // Devices with an inaccurate date/time will not be able to load the mapbox map
                    // If the "automatic date/time" is disabled on the device and the map fails to load, recommend the user enable it
                } else if (Settings.Global.getInt(getContext().getContentResolver(), Settings.Global.AUTO_TIME, 0) == 0) {
                    for (AirMapMapView.OnMapLoadListener mapLoadListener : mapLoadListeners) {
                        mapLoadListener.onMapFailed(AirMapMapView.MapFailure.INACCURATE_DATE_TIME_FAILURE);
                    }

                    // check connection by requesting the styles json directly (async)
                } else {
                    mapStyleController.checkConnection(new AirMapCallback<Void>() {
                        @Override
                        protected void onSuccess(Void response) {
                            for (AirMapMapView.OnMapLoadListener mapLoadListener : mapLoadListeners) {
                                mapLoadListener.onMapFailed(AirMapMapView.MapFailure.UNKNOWN_FAILURE);
                            }

                            String logs = Utils.getMapboxLogs();
                            Analytics.report(new Exception("Mapbox map failed to load due to no network connection but able to access styles directly: " + logs));
                        }

                        @Override
                        protected void onError(AirMapException e) {
                            for (AirMapMapView.OnMapLoadListener mapLoadListener : mapLoadListeners) {
                                mapLoadListener.onMapFailed(AirMapMapView.MapFailure.NETWORK_CONNECTION_FAILURE);
                            }
                        }
                    });
                    return;
                }*/
                break;
            }
            case MapView.REGION_DID_CHANGE:
            case MapView.REGION_DID_CHANGE_ANIMATED: {
                if (mapDataController != null) {
                    mapDataController.onMapRegionChanged();
                }
                break;
            }
            case MapView.DID_FINISH_RENDERING_MAP_FULLY_RENDERED: {
                if (mapDataController != null) {
                    mapDataController.onMapFinishedRendering();
                }
                break;
            }
            default: {
                break;
            }
        }
    }


    @Override
    public void getMapAsync(final OnMapReadyCallback callback) {
        super.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                map.addOnMapClickListener(AutelAirmapView.this);
                map.addOnMapLongClickListener(AutelAirmapView.this);
                map.getUiSettings().setLogoGravity(Gravity.BOTTOM | Gravity.END); // Move to bottom right
                map.getUiSettings().setAttributionGravity(Gravity.BOTTOM | Gravity.END); // Move to bottom right
                map.getUiSettings().setAllGesturesEnabled(true);
                setupMapDragging(map);
                mapStyleController.onMapReady();
                if (callback != null) {
                    callback.onMapReady(mapboxMap);
                }
            }
        });
    }

    private void setupMapDragging(MapboxMap map) {

        final float screenDensity = getResources().getDisplayMetrics().density;

    }
}
