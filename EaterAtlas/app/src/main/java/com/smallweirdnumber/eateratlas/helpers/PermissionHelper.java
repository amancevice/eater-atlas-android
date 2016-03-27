package com.smallweirdnumber.eateratlas.helpers;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.text.Html;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.smallweirdnumber.eateratlas.R;
import com.smallweirdnumber.eateratlas.activities.MapsActivity;

public class PermissionHelper {

    private static MapsActivity mMapsActivity;

    /**
     * Bind static class to the main activity
     * @param context Main activity
     */
    public static void bind(Context context) {
        mMapsActivity = (MapsActivity) context;
    }

    /**
     * Get last location, asking for permission if necessary.
     * @param client Google API client
     * @return last known Location
     */
    public static Location getLastLocation(GoogleApiClient client) {
        if (client != null && hasPermission()) {
            try {
                return LocationServices.FusedLocationApi.getLastLocation(client);
            } catch (SecurityException e) {
                requestPermissionAsync();
            }
        }
        return null;
    }

    /**
     * Add "my location" dot on the map.
     */
    public static void setMyLocationEnabled() {
        GoogleMap map = MapsHelper.getMap();
        if (mMapsActivity != null && map != null && hasPermission()) {
            try {
                map.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                requestPermissionAsync();
            }
        }
    }

    /**
     * Show dialog explaining why location services are needed.
     */
    public static void showRationale() {
        // Display UI and wait for user interaction
        if (mMapsActivity != null) {
            new AlertDialog.Builder(mMapsActivity)
                    .setMessage(Html.fromHtml(
                            mMapsActivity.getResources().getString(R.string.rationale)))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    }).create().show();
        }
    }

    /**
     * Assert app has permission to access location.
     * If not, request it asynchronously.
     * @return True if perms eneabled, else false
     */
    public static boolean hasPermission() {
        if (mMapsActivity != null) {
            if (ActivityCompat.checkSelfPermission(mMapsActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(mMapsActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        requestPermissionAsync();
        return false;
    }

    /**
     * Request permission to access location asynchronously.
     */
    private static void requestPermissionAsync() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                requestPermission();
                return null;
            }
        }.execute();
    }

    /**
     * Request permission to access location.
     */
    private static void requestPermission() {
        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(
                mMapsActivity, permissions, MapsActivity.REQUEST_LOCATION);
    }

}
