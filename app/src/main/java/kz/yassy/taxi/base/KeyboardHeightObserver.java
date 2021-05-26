package kz.yassy.taxi.base;

import android.content.res.Configuration;

public interface KeyboardHeightObserver {

    /**
     * Called when the keyboard height has changed, 0 means keyboard is closed,
     * > 0 means keyboard is opened.
     *
     * @param height      The height of the keyboard in pixels
     * @param orientation The orientation either: {@link Configuration#ORIENTATION_PORTRAIT ORIENTATION_PORTRAIT} or
     *                    {@link Configuration#ORIENTATION_LANDSCAPE ORIENTATION_LANDSCAPE}
     */
    void onKeyboardHeightChanged(int height, int orientation);

}
