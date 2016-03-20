package com.smallweirdnumber.eateratlas.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.smallweirdnumber.eateratlas.helpers.BottomPanel;
import com.smallweirdnumber.eateratlas.helpers.FireData;
import com.smallweirdnumber.eateratlas.helpers.MapsHelper;
import com.smallweirdnumber.eateratlas.helpers.NavDrawer;
import com.smallweirdnumber.eateratlas.helpers.PermissionHelper;
import com.smallweirdnumber.eateratlas.helpers.TopMenu;
import com.smallweirdnumber.eateratlas.R;

public class MapsActivity extends FragmentActivity {

    public static final int REQUEST_LOCATION = 2;
    private final int SHADE_REQUEST = 1;
    private static boolean mRedraw = true;
    private static boolean mStartup = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Bind permission helper
        PermissionHelper.bind(this);

        // Set up Firebase
        FireData.bind(this);

        // Set up map
        MapsHelper.bind(this);

        // Set up panel
        BottomPanel.bind(this);

        // Set up top menu
        TopMenu.bind(this);
        TopMenu.update();

        // Set up drawer
        NavDrawer.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRedraw) {
            BottomPanel.reset();
            TopMenu.update();
            mRedraw = MapsHelper.drawMap();
        }
    }

    @Override
    public void onBackPressed() {
        // Collapse panel or go back as normal
        if (!BottomPanel.goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Hide the shade
        if (requestCode == SHADE_REQUEST) {
            View shade = findViewById(R.id.picker_shade);
            if (shade != null) {
                shade.setVisibility(View.GONE);
            }
            TopMenu.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                for (int grantResult : grantResults) {
                    switch (grantResult) {
                        case PackageManager.PERMISSION_GRANTED: {
                            PermissionHelper.setMyLocationEnabled();
                            MapsHelper.moveToLastLocation();
                            return;
                        }
                        case PackageManager.PERMISSION_DENIED: {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.ACCESS_FINE_LOCATION)
                                    || ActivityCompat.shouldShowRequestPermissionRationale(
                                    this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                                    PermissionHelper.showRationale();
                                    return;
                            }
                        }
                    }
                }
            }
        }
    }

    public void openWeekdayActivity() {
        Intent intent = new Intent(this, WeekdaysActivity.class);
        startActivityForResult(intent, SHADE_REQUEST);
        overridePendingTransition(0, 0);
    }

    public void openMealActivity() {
        Intent intent = new Intent(this, MealsActivity.class);
        startActivityForResult(intent, SHADE_REQUEST);
        overridePendingTransition(0, 0);
    }

    public boolean isStarting() {
        return mStartup;
    }

    public void markStarted() {
        mStartup = false;
    }

    public void setRedraw(boolean redraw) {
        mRedraw = redraw;
    }
}
