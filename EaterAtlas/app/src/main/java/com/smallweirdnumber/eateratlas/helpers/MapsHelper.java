package com.smallweirdnumber.eateratlas.helpers;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smallweirdnumber.eateratlas.R;
import com.smallweirdnumber.eateratlas.activities.MapsActivity;

public class MapsHelper {
    public static final float WORLD_ZOOM = 3;
    public static final float CITY_ZOOM = 12;
    public static final float ZOOM = 15;
    private static final int SLIDE_DELAY = 1000;
    private static MapsActivity mMapsActivity;
    private static GoogleMap mMap;
    private static Marker mMarkerSelected;
    private static View mSplash;
    private static BitmapDescriptor mMarkerIconSelected;
    private static BitmapDescriptor mMarkerIcon;
    private static GoogleApiClient mGoogleApiClient;

    public static void bind(Context context) {
        mMapsActivity = (MapsActivity) context;
        mSplash = findViewById(R.id.splash);
        SupportMapFragment mapFragment = getMapFragment(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new MapReadyCallback());
        }
    }

    public static boolean drawMap() {
        if (mMap != null && mMarkerIcon != null) {
            // Reset markers
            mMap.clear();

            // Drop & store markers
            for (LatLng latLng : FireData.getLatLngs()) {
                mMap.addMarker(new MarkerOptions().position(latLng).icon(mMarkerIcon));
            }

            // Mark redrawn
            return false;
        } else {
            return true;
        }
    }

    public static void deselectMarker() {
        if (mMarkerSelected != null) {
            mMarkerSelected.remove();
        }
    }

    public static void selectMarker(Marker marker) {
        deselectMarker();
        mMarkerSelected = MapsHelper.getMap().addMarker(
                new MarkerOptions().position(marker.getPosition()).icon(mMarkerIconSelected));
    }

    public static Marker getSelectedMarker() {
        return mMarkerSelected;
    }

    public static GoogleMap getMap() {
        return mMap;
    }

    public static Location lastLocation() {
        return PermissionHelper.getLastLocation(mGoogleApiClient);
    }

    public static void moveToLastLocation(float zoom) {
        Location location = lastLocation();
        if (location != null && mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), zoom));
        }
    }

    public static void moveToLastLocation() {
        moveToLastLocation(ZOOM);
    }

    public static void animateToLastLocation(float zoom) {
        Location location = lastLocation();
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            animateToLocation(latLng, zoom);
        }
    }

    public static void animateToLastLocation() {
        if (mMap != null) {
            float zoom = mMap.getCameraPosition().zoom;
            if (zoom < ZOOM) {
                zoom = ZOOM;
            }
            animateToLastLocation(zoom);
        }
    }

    public static void animateToLocation(LatLng latLng, float zoom) {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
    }

    public static void animateToWorld() {
        LatLng center = new LatLng(42.479277, -96.607836);
        animateToLocation(center, WORLD_ZOOM);
    }

    /* Private */

    private static View findViewById(int id) {
        if (mMapsActivity != null) {
            return mMapsActivity.findViewById(id);
        } else {
            return null;
        }
    }

    private static SupportMapFragment getMapFragment(int id) {
        if (mMapsActivity != null) {
            return (SupportMapFragment) mMapsActivity
                    .getSupportFragmentManager().findFragmentById(id);
        } else {
            return null;
        }
    }

    private static class MapReadyCallback implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setOnMarkerClickListener(new MarkerClick());
            mMap.setOnMapClickListener(new MapClick());
            mMap.setOnMapLoadedCallback(new MapLoadedCallback());

            mMarkerIcon = BitmapDescriptorFactory.fromResource(R.mipmap.map_marker);
            mMarkerIconSelected = BitmapDescriptorFactory.fromResource(R.mipmap.map_marker_orange);

            // Slide out splash screen
            if (mSplash != null) {
                mSplash.animate()
                        .translationY(ScreenHelper.getScreenMetrics(mMapsActivity).heightPixels)
                        .setStartDelay(SLIDE_DELAY);
            }
        }
    }

    private static class MapLoadedCallback implements GoogleMap.OnMapLoadedCallback {
        @Override
        public void onMapLoaded() {
            mGoogleApiClient = new GoogleApiClient.Builder(mMapsActivity)
                    .addConnectionCallbacks(new GoogleConnectionCallbacks())
                    .addApi(LocationServices.API).build();
            mGoogleApiClient.connect();
        }
    }

    private static class GoogleConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(Bundle bundle) {
            if (PermissionHelper.hasPermission()) {
                MapsHelper.moveToLastLocation();
                PermissionHelper.setMyLocationEnabled();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    }

    public static class MarkerClick implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            // Set marker icons
            MapsHelper.selectMarker(marker);

            // Enable Panel
            BottomPanel.enable();
            BottomPanel.setHeaderFromSelectedMarker();

            // Show trucks
            BottomPanel.fillPanelWithTrucksAtSelectedMarker();

            // Move camera
            return false;
        }
    }

    private static class MapClick implements GoogleMap.OnMapClickListener {

        @Override
        public void onMapClick(LatLng latLng) {
            BottomPanel.collapse();
            BottomPanel.reset();

            // Reset selected marker
            MapsHelper.deselectMarker();
        }
    }
}
