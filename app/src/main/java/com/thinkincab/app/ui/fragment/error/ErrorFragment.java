package com.thinkincab.app.ui.fragment.error;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IntRange;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseBottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ErrorFragment extends BaseBottomSheetDialogFragment {

    public static final int LOCATION = 0;
    public static final int AREA = 1;
    private static final String TYPE = "type.extra";

    @BindView(R.id.form_title)
    TextView errorTitle;
    @BindView(R.id.form_desc)
    TextView errorDesc;


    public static ErrorFragment newInstance(@IntRange(from = 0, to = 1) int type) {
        ErrorFragment fragment = new ErrorFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_error;
    }

    @Override
    protected void initView(View view) {
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            int type = getArguments().getInt(TYPE);
            if (type == LOCATION) {
                errorTitle.setText(R.string.error_location_title);
                errorDesc.setText(R.string.error_location_desc);
            } else if (type == AREA) {
                errorTitle.setText(R.string.error_area_title);
                errorDesc.setText("");
            }
        }
    }
}
