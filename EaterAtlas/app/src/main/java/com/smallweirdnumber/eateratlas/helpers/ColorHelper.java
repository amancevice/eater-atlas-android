package com.smallweirdnumber.eateratlas.helpers;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;


public class ColorHelper {
    public static int getColor(Context context, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(color);
        } else {
            return ContextCompat.getColor(context, color);
        }
    }

    public static int getBackgroundColor(Context context, View v, int defaultColor) {
        Drawable drawable = v.getBackground();
        if (drawable instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            return colorDrawable.getColor();
        } else {
            return ColorHelper.getColor(context, defaultColor);
        }
    }

}
