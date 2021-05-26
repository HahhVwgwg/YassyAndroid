package kz.yassy.taxi.base;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import kz.yassy.taxi.R;

public class KeyboardHeightProvider extends PopupWindow {

    /**
     * The tag for logging purposes
     */
    private final static String TAG = KeyboardHeightProvider.class.getName();

    /**
     * The view that is used to calculate the keyboard height
     */
    private View popupView;

    /**
     * The parent view
     */
    private View parentView;

    /**
     * The root activity that uses this KeyboardHeightProvider
     */
    private Activity activity;

    /**
     * The cached landscape height of the keyboard
     */
    private int keyboardLandscapeHeight, keyboardLandscapeHeightDelta;

    /**
     * The cached portrait height of the keyboard
     */
    private int keyboardPortraitHeight, keyboardPortraitHeightDelta;

    /**
     * The keyboard height observer
     */
    private KeyboardHeightObserver observer;

    /**
     * Construct a new KeyboardHeightProvider
     *
     * @param activity The parent activity
     */
    public KeyboardHeightProvider(Activity activity) {
        super(activity);
        this.activity = activity;

        this.popupView = LayoutInflater.from(activity).inflate(R.layout.keyboard_popup, null, false);
        setContentView(popupView);

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        parentView = activity.findViewById(android.R.id.content);

        setWidth(0);
        setHeight(LayoutParams.MATCH_PARENT);

        popupView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (popupView != null) {
                handleOnGlobalLayout();
            }
        });
    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    public void start() {
        if (!isShowing() && parentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    /**
     * Close the keyboard height provider,
     * this provider will not be used anymore.
     */
    public void close() {
        observer = null;
        dismiss();
    }

    /**
     * Set the keyboard height observer to this provider. The
     * observer will be notified when the keyboard height has changed.
     * For example when the keyboard is opened or closed.
     *
     * @param observer The observer to be added to this provider.
     */
    public void setKeyboardHeightObserver(KeyboardHeightObserver observer) {
        this.observer = observer;
    }

    /**
     * Get cached keyboard height for current screen orientation.
     * If keyboard was not yet shown for current orientation, value is undefined.
     *
     * @return cached keyboard height
     */
    public int getKeyboardHeight() {
        return getKeyboardHeight(getScreenOrientation());
    }

    public Bundle onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putInt("keyboardPortraitHeightDelta", keyboardPortraitHeightDelta);
        state.putInt("keyboardLandscapeHeightDelta", keyboardLandscapeHeightDelta);
        return state;
    }

    public void onRestoreInstanceState(Bundle state) {
        keyboardPortraitHeightDelta = state.getInt("keyboardPortraitHeightDelta");
        keyboardLandscapeHeightDelta = state.getInt("keyboardLandscapeHeightDelta");
    }

    /**
     * Get cached keyboard height for given screen orientation.
     * If keyboard was not yet shown for given orientation, value is undefined.
     *
     * @param orientation one of {@link Configuration#ORIENTATION_PORTRAIT ORIENTATION_PORTRAIT} or {@link Configuration#ORIENTATION_LANDSCAPE ORIENTATION_LANDSCAPE}
     * @return cached keyboard height
     */
    public int getKeyboardHeight(int orientation) {
        return orientation == Configuration.ORIENTATION_PORTRAIT ? keyboardPortraitHeight : keyboardLandscapeHeight;
    }

    /**
     * Get the screen orientation
     *
     * @return the screen orientation
     */
    private int getScreenOrientation() {
        return activity.getResources().getConfiguration().orientation;
    }

    /**
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity window height.
     */
    private void handleOnGlobalLayout() {
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        int orientation = getScreenOrientation();
        int keyboardHeight = screenSize.y - rect.bottom;

        // fix for phones that give "getWindowVisibleDisplayFrame()" in real screen size
        // but screenSize is smaller because of e.g. carved out display portion (Xiaomi redmi 9)
        if (keyboardHeight <= 0) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                keyboardPortraitHeightDelta = keyboardHeight;
            } else {
                keyboardLandscapeHeightDelta = keyboardHeight;
            }
            notifyKeyboardHeightChanged(0, orientation);
            return;
        }

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            keyboardHeight -= keyboardPortraitHeightDelta;
            keyboardPortraitHeight = keyboardHeight;
        } else {
            keyboardHeight -= keyboardLandscapeHeightDelta;
            keyboardLandscapeHeight = keyboardHeight;
        }
        notifyKeyboardHeightChanged(keyboardHeight, orientation);
    }

    private void notifyKeyboardHeightChanged(int height, int orientation) {
        if (observer != null) {
            observer.onKeyboardHeightChanged(height, orientation);
        }
    }

}

