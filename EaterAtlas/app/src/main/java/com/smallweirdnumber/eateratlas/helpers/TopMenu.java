package com.smallweirdnumber.eateratlas.helpers;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

import com.smallweirdnumber.eateratlas.R;
import com.smallweirdnumber.eateratlas.activities.MapsActivity;
import com.smallweirdnumber.eateratlas.activities.WeekdaysActivity;


public class TopMenu {
    private static final int SHADE_REQUEST = 1;
    private static MapsActivity mMapsActivity;
    private static View mMenu, mWeekday, mMeal, mShade;
    private static TextView mWeekdayText, mMealText;

    public static void bind(Context context) {
        mMapsActivity = (MapsActivity) context;
        mMenu = findViewById(R.id.menu);
        mWeekday = findViewById(R.id.menu_weekday);
        mMeal = findViewById(R.id.menu_meal);
        mShade = findViewById(R.id.picker_shade);

        mWeekdayText = (TextView) findViewById(R.id.menu_weekday_text);
        mMealText = (TextView) findViewById(R.id.menu_meal_text);
        mWeekday.setOnClickListener(new WeekdayClickListener());
        mMeal.setOnClickListener(new MealClickListener());

        View drawer = findViewById(R.id.menu_drawer);
        View myLocation = findViewById(R.id.menu_my_location);
        if (drawer != null) {
            drawer.setOnClickListener(new DrawerClickListener());
        }
        if (myLocation != null) {
            myLocation.setOnClickListener(new MyLocationClickListener());
        }
    }

    public static void update() {
        setTextView(mWeekdayText, FireData.getWeekday().toString());
        setTextView(mMealText, FireData.getMeal().toString());
    }

    public static void hide() {
        setVisibility(View.INVISIBLE);
    }

    public static void show() {
        setVisibility(View.VISIBLE);
    }

    private static View findViewById(int id) {
        if (mMapsActivity != null) {
            return mMapsActivity.findViewById(id);
        } else {
            return null;
        }
    }

    private static void setVisibility(int visibility) {
        if (mMenu != null) {
            mMenu.setVisibility(visibility);
        }
    }

    private static void setTextView(TextView view, String text) {
        if (view != null) {
            view.setText(text);
        }
    }

    private static Animator getOrangeAnimator(View v) {
        int[] xy = ScreenHelper.getCenterOfView(v);
        float radius = ScreenHelper.getScreenMetrics(mMapsActivity).heightPixels;
        return ViewAnimationUtils.createCircularReveal(mShade, xy[0], xy[1], 0, radius);
    }

    private static class DrawerClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            NavDrawer.open();
        }
    }

    private static class WeekdayClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            hide();
            Animator anim = getOrangeAnimator(mWeekday);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    Intent intent = new Intent(mMapsActivity, WeekdaysActivity.class);
                    mMapsActivity.startActivityForResult(intent, SHADE_REQUEST);
                    mMapsActivity.overridePendingTransition(0, 0);
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            mShade.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    private static class MealClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            hide();
            Animator anim = getOrangeAnimator(mMeal);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    mMapsActivity.openMealActivity();
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            mShade.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    private static class MyLocationClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MapsHelper.animateToLastLocation();
        }
    }
}
