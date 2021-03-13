package com.thinkincab.app.ui.utils;

import android.content.res.Resources;

public class DisplayUtils {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float dpToPxInFloat(int dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }
}
