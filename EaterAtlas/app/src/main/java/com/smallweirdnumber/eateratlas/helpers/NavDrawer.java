package com.smallweirdnumber.eateratlas.helpers;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.smallweirdnumber.eateratlas.R;
import com.smallweirdnumber.eateratlas.activities.MapsActivity;

public class NavDrawer {

    private static MapsActivity mMapsActivity;
    private static DrawerLayout mDrawerLayout;

    public static void bind(Context context) {
        mMapsActivity = (MapsActivity) context;
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        View city = findViewById(R.id.drawer_city);
        View meal = findViewById(R.id.drawer_meal);
        View weekday = findViewById(R.id.drawer_weekday);

        // Set OnClickListeners
        if (city != null) {
            city.setOnClickListener(new CityClickListener());
        }
        if (meal != null) {
            meal.setOnClickListener(new MealClickListener());
        }
        if (weekday != null) {
            weekday.setOnClickListener(new WeekdayClickListener());
        }
    }

    public static void close() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void open() {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }
    private static View findViewById(int id) {
        if (mMapsActivity != null) {
            return mMapsActivity.findViewById(id);
        } else {
            return null;
        }
    }

    private static class CityClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            BottomPanel.reset();
            close();
            final GoogleMap map = MapsHelper.getMap();
            if (map != null) {
                // Zoom out and clear map
                MapsHelper.animateToWorld();

                // Set temporary OnMarkerClickListener
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        MapsHelper.animateToLocation(
                                marker.getPosition(), MapsHelper.CITY_ZOOM);
                        map.setOnMarkerClickListener(new MapsHelper.MarkerClick());
                        return true;
                    }
                });
            }
        }
    }

    private static class WeekdayClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mDrawerLayout != null && mMapsActivity != null) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                mMapsActivity.openWeekdayActivity();
            }
        }
    }

    private static class MealClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mDrawerLayout != null && mMapsActivity != null) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                mMapsActivity.openMealActivity();
            }
        }
    }
}
