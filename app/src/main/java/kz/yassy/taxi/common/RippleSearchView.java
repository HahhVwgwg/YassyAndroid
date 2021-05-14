package kz.yassy.taxi.common;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import kz.yassy.taxi.R;

public class RippleSearchView extends RelativeLayout {

    private final static int DEFAULT_RIPPLE_COUNT = 4;
    private final static int DEFAULT_DURATION_TIME = 3000;
    private final static float DEFAULT_SCALE = 6.0f;
    private final static int DEFAULT_FILL_TYPE = 0;

    private float rippleStrokeWidth = 0f;
    private Paint paint = new Paint();
    private boolean animationRunning = false;
    private AnimatorSet animatorSet;
    private final List<RippleView> rippleViewList = new ArrayList<>();

    public RippleSearchView(Context context) {
        this(context, null);
    }

    public RippleSearchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleSearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RippleSearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (!isInEditMode()) {
            if (attrs != null) {
                final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RippleSearchView);
                int rippleColor = typedArray.getColor(R.styleable.RippleSearchView_rb_color, ContextCompat.getColor(getContext(), R.color.app_pink_search));
                rippleStrokeWidth = typedArray.getDimension(R.styleable.RippleSearchView_rb_strokeWidth, getResources().getDimension(R.dimen.rippleStrokeWidth));
                float rippleRadius = typedArray.getDimension(R.styleable.RippleSearchView_rb_radius, getResources().getDimension(R.dimen.rippleRadius));
                long rippleDurationTime = (long) typedArray.getInt(R.styleable.RippleSearchView_rb_duration, DEFAULT_DURATION_TIME);
                int rippleAmount = typedArray.getInt(R.styleable.RippleSearchView_rb_rippleAmount, DEFAULT_RIPPLE_COUNT);
                float rippleScale = typedArray.getFloat(R.styleable.RippleSearchView_rb_scale, DEFAULT_SCALE);
                int rippleType = typedArray.getInt(R.styleable.RippleSearchView_rb_type, DEFAULT_FILL_TYPE);
                typedArray.recycle();

                long rippleDelay = rippleDurationTime / rippleAmount;

                paint = new Paint();
                paint.setAntiAlias(true);
                if (rippleType == DEFAULT_FILL_TYPE) {
                    rippleStrokeWidth = 0f;
                    paint.setStyle(Paint.Style.FILL);
                } else {
                    paint.setStyle(Paint.Style.STROKE);
                }
                paint.setColor(rippleColor);

                LayoutParams rippleParams = new LayoutParams(
                        (int) (2 * (rippleRadius + rippleStrokeWidth)),
                        (int) (2 * (rippleRadius + rippleStrokeWidth))
                );
                rippleParams.addRule(CENTER_IN_PARENT, TRUE);

                animatorSet = new AnimatorSet();
                animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                List<Animator> animatorList = new ArrayList<>();
                for (int i = 0; i < rippleAmount; i++) {
                    RippleView rippleView = new RippleView(getContext());
                    addView(rippleView, rippleParams);
                    rippleViewList.add(rippleView);
                    ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale);
                    scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                    scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
                    scaleXAnimator.setStartDelay(i * rippleDelay);
                    scaleXAnimator.setDuration(rippleDurationTime);
                    animatorList.add(scaleXAnimator);
                    ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale);
                    scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                    scaleYAnimator.setRepeatMode(ObjectAnimator.RESTART);
                    scaleYAnimator.setStartDelay(i * rippleDelay);
                    scaleYAnimator.setDuration(rippleDurationTime);
                    animatorList.add(scaleYAnimator);
                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f);
                    alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                    alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
                    alphaAnimator.setStartDelay(i * rippleDelay);
                    alphaAnimator.setDuration(rippleDurationTime);
                    animatorList.add(alphaAnimator);
                }
                animatorSet.playTogether(animatorList);
            }
        }
    }

    public void startRippleAnimation() {
        if (!isRippleAnimationRunning()) {
            for (View rippleView : rippleViewList) {
                rippleView.setVisibility(View.VISIBLE);
            }
            animatorSet.start();
            animationRunning = true;
        }
    }

    public void stopRippleAnimation() {
        if (isRippleAnimationRunning()) {
            animatorSet.end();
            animationRunning = false;
        }
    }

    private boolean isRippleAnimationRunning() {
        return animationRunning;
    }

    private class RippleView extends View {

        public RippleView(Context context) {
            this(context, null);
        }

        public RippleView(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            this(context, attrs, defStyleAttr, 0);
        }

        public RippleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            init();
        }

        private void init() {
            this.setVisibility(INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float radius = (float) (Math.min(getWidth(), getHeight()) / 2);
            canvas.drawCircle(radius, radius, radius - rippleStrokeWidth, paint);
        }
    }
}
