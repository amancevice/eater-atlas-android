package com.smallweirdnumber.eateratlas.helpers;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class ScreenHelper {
    public static DisplayMetrics getScreenMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    /*public static int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    public static int getPixelsFromPercentScreenHeight(Context context, float percent) {
        return getPixelsFromPercent(percent, getScreenMetrics(context).heightPixels);
    }*/

    public static int getPixelsFromPercent(float percent, int height) {
        return (int) Math.floor(((double) height) * ((double) percent));
    }

    public static int[] getCenterOfView(View view) {
        int locationOnScreen[] = new int[2];
        view.getLocationOnScreen(locationOnScreen);
        int x = locationOnScreen[0] + (view.getWidth()/2);
        int y = locationOnScreen[1] + (view.getHeight()/2);
        return new int[] {x, y};
    }
}
