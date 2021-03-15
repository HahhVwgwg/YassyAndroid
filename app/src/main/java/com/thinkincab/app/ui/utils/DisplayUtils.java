package com.thinkincab.app.ui.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.Nullable;

public class DisplayUtils {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float dpToPxInFloat(int dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    public static int getScreenWidth(@Nullable Activity ctx) {
        if (ctx == null) {
            return 0;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getCustomWidth(@Nullable Activity ctx, int parts, int margins) {

        int r = (getScreenWidth(ctx) - dpToPx(margins)) / parts;
        Log.d("sdggds", r + ", " + getScreenWidth(ctx));
        return r;
    }
}
