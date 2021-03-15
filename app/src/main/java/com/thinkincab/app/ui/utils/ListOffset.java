package com.thinkincab.app.ui.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListOffset extends RecyclerView.ItemDecoration {

    public static int TOP = 0;
    public static int START = 1;

    private final int space;
    private final int direction;

    public ListOffset(int space) {
        this.space = space;
        this.direction = TOP;
    }

    public ListOffset(int space, @IntRange(from = 0, to = 1) int direction) {
        this.space = space;
        this.direction = direction;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildLayoutPosition(view) == 0) {
            if (direction == TOP) {
                outRect.top = space;
            } else if (direction == START) {
                outRect.left = space;
            }

        }
    }
}
