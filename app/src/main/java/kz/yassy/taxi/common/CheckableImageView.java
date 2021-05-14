package kz.yassy.taxi.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CheckableImageView extends AppCompatImageView implements Checkable {

    private final static int[] CHECKED_STATE_SET = new int[]{android.R.attr.state_checked};

    private boolean stateChecked = false;

    public CheckableImageView(@NonNull Context context) {
        super(context);
    }

    public CheckableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setChecked(boolean b) {
        stateChecked = b;
        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return stateChecked;
    }

    @Override
    public void toggle() {
        stateChecked = !stateChecked;
        refreshDrawableState();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }
}
