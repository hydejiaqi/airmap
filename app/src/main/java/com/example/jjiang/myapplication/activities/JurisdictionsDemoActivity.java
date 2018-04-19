package com.example.jjiang.myapplication.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.airmap.airmapsdk.AirMapException;
import com.airmap.airmapsdk.AirMapLog;
import com.airmap.airmapsdk.models.AirMapWeather;
import com.airmap.airmapsdk.models.Coordinate;
import com.airmap.airmapsdk.models.flight.AirMapFlight;
import com.airmap.airmapsdk.models.flight.AirMapFlightBriefing;
import com.airmap.airmapsdk.models.flight.AirMapFlightPlan;
import com.airmap.airmapsdk.models.rules.AirMapJurisdiction;
import com.airmap.airmapsdk.models.shapes.AirMapPolygon;
import com.airmap.airmapsdk.models.status.AirMapAirspaceStatus;
import com.airmap.airmapsdk.models.traffic.AirMapTraffic;
import com.airmap.airmapsdk.networking.callbacks.AirMapCallback;
import com.airmap.airmapsdk.networking.callbacks.AirMapTrafficListener;
import com.airmap.airmapsdk.networking.services.AirMap;
import com.example.jjiang.myapplication.R;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class JurisdictionsDemoActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = "JurisdictionsDemo";

    private Toolbar toolbar;
    private MapView mapView;

    private MapboxMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(MapboxMap map) {
        this.map = map;

        map.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                updateJurisdictions();
            }
        });

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(34.0195, -118.4912), 13));
    }

    private void updateJurisdictions() {
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(bounds.getLatNorth(), bounds.getLonWest()));
        coordinates.add(new Coordinate(bounds.getLatNorth(), bounds.getLonEast()));
        coordinates.add(new Coordinate(bounds.getLatSouth(), bounds.getLonEast()));
        coordinates.add(new Coordinate(bounds.getLatSouth(), bounds.getLonWest()));
        coordinates.add(new Coordinate(bounds.getLatNorth(), bounds.getLonWest()));

        final AirMapPolygon polygon = new AirMapPolygon(coordinates);

        AirMap.getJurisdictions(polygon, new AirMapCallback<List<AirMapJurisdiction>>() {
            @Override
            protected void onSuccess(List<AirMapJurisdiction> jurisdictions) {
                AirMapLog.d(TAG, "Jurisdictions: " + jurisdictions);

                // Available jurisdictions and their rulesets
                for (AirMapJurisdiction jurisdiction : jurisdictions) {
                    AirMapLog.d(TAG, "Jurisdiction: " + jurisdiction);
                    AirMapLog.d(TAG, "Pick One rulesets: " + jurisdiction.getPickOneRulesets());
                    AirMapLog.d(TAG, "Optional rulesets: " + jurisdiction.getOptionalRulesets());
                    AirMapLog.d(TAG, "Required rulesets: " + jurisdiction.getRequiredRulesets());
                }

                // Display jurisdictions and their respective groups of rulesets
            }

            @Override
            protected void onError(AirMapException e) {
                AirMapLog.e(TAG, "Unable to get jurisdictions", e);
                // Handle error
            }
        });

        final List<String> rulesetIds = Arrays.asList("usa_part_107");

        AirMap.getAirspaceStatus(polygon, rulesetIds, new AirMapCallback<AirMapAirspaceStatus>() {
            @Override
            protected void onSuccess(AirMapAirspaceStatus status) {
                // Show status advisories
                AirMapLog.d(TAG, "Status: " + status);
            }

            @Override
            protected void onError(AirMapException e) {
                // Handle error
            }
        });

        final Coordinate coordinate = new Coordinate(map.getCameraPosition().target);
        Date startTime = new Date();
        Date endTime = new Date(startTime.getTime() + (4 * 60 * 60 * 1000));

        AirMap.getWeather(coordinate, startTime, endTime, new AirMapCallback<AirMapWeather>() {
            @Override
            protected void onSuccess(AirMapWeather weather) {
                AirMapLog.d(TAG, "Weather: " + weather.getUpdates());
            }

            @Override
            protected void onError(AirMapException e) {
                // Handle error
            }
        });

        String userId = "acme|123";

        AirMap.performAnonymousLogin(userId, new AirMapCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                AirMapLog.v(TAG, "Token is: " + AirMap.getAuthToken());

                // handle login
                createFlightPlan(polygon, coordinate, rulesetIds);
            }

            @Override
            public void onError(AirMapException e) {
                AirMapLog.e(TAG, e.getDetailedMessage(), e);

                // handle error here
            }
        });
    }

    private void createFlightPlan(AirMapPolygon polygon, Coordinate takeoffCoordinate, List<String> rulesetIds) {
        // sets required params
        AirMapFlightPlan flightPlan = new AirMapFlightPlan();
        flightPlan.setPilotId(AirMap.getUserId());
        flightPlan.setGeometry(polygon);
        flightPlan.setBuffer(100);
        flightPlan.setTakeoffCoordinate(takeoffCoordinate);
        flightPlan.setRulesetIds(rulesetIds);

        flightPlan.setMaxAltitude(100);

        // default start & end time - now to 4 hours from now
        long duration = 4 * 60 * 60 * 1000;
        flightPlan.setDurationInMillis(duration);
        flightPlan.setStartsAt(new Date());
        flightPlan.setEndsAt(new Date(System.currentTimeMillis() + duration));

        AirMap.createFlightPlan(flightPlan, new AirMapCallback<AirMapFlightPlan>() {
            @Override
            protected void onSuccess(AirMapFlightPlan response) {
                AirMapLog.d(TAG, "Flight plan created: " + response.getPlanId());
                // Handle success

                fly(response.getPlanId());
            }

            @Override
            protected void onError(AirMapException e) {
                AirMapLog.e(TAG, "Failed to create flight plan", e);
                // Handle error
            }
        });
    }

    private void getFlightBriefing(String flightPlanId) {
        AirMap.getFlightBrief(flightPlanId, new AirMapCallback<AirMapFlightBriefing>() {
            @Override
            protected void onSuccess(AirMapFlightBriefing briefing) {
                // Show user advisory status, authorizations and validations
            }

            @Override
            protected void onError(AirMapException e) {
                // Handle error
            }
        });
    }

    private void fly(String flightPlanId) {
        AirMap.submitFlightPlan(flightPlanId, new AirMapCallback<AirMapFlightPlan>() {
            @Override
            protected void onSuccess(AirMapFlightPlan flightPlan) {
                AirMapLog.d(TAG, "Flight id: " + flightPlan.getFlightId());
            }

            @Override
            protected void onError(AirMapException e) {
                // Handle error case
            }
        });
    }

    private void sendTelemetry() {
        AirMapFlight flight = null;

//        AirMap.getTelemetryService().sendAttitudeMessage(flight, yaw, pitch, roll);
//
//        AirMap.getTelemetryService().sendPositionMessage(flight, lat, longitude, altAGL, altMSL, horizontalAccuracy);
//
//        AirMap.getTelemetryService().sendSpeedMessage(flight, speedX, speedY, speedZ);
//
//        AirMap.getTelemetryService().setBarometerMessage(flight, pressure);
    }

    private void getTraffic() {
        // To turn on traffic alerts
        AirMap.enableTrafficAlerts(new AirMapTrafficListener() {
            @Override
            public void onAddTraffic(List<AirMapTraffic> added) {
                // Display traffic on map
            }

            @Override
            public void onUpdateTraffic(List<AirMapTraffic> updated) {
                // Update traffic on map
            }

            @Override
            public void onRemoveTraffic(List<AirMapTraffic> removed) {
                // Remove traffic from map
            }
        });

        // To turn off traffic alerts
        AirMap.disableTrafficAlerts();
    }

    private void endFlight(String flightId) {
        // only call end if the flight is active
        AirMap.endFlight(flightId, new AirMapCallback<AirMapFlight>() {
            @Override
            public void onSuccess(AirMapFlight response) {
                // Handle success (remove from map, disable traffic alerts, etc)
            }

            @Override
            public void onError(AirMapException e) {
                // Handle error
            }
        });

    }

    // Mapbox requires lifecycle
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
