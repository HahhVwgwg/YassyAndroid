package com.thinkincab.app.ui.fragment.sos;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseBottomSheetDialogFragment;
import com.thinkincab.app.ui.fragment.searching.SearchingFragment;
import com.thinkincab.app.ui.fragment.searching.SearchingPresenter;

import butterknife.ButterKnife;

public class SosFragment extends BaseBottomSheetDialogFragment {

    //private SearchingPresenter<SearchingFragment> presenter = new SearchingPresenter<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sos;
    }

    @Override
    public void initView(View view) {
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        ButterKnife.bind(this, view);
       // presenter.attachView(this);
    }
}
