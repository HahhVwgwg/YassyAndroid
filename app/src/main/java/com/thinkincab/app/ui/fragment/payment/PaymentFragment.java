package com.thinkincab.app.ui.fragment.payment;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseBottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentFragment extends BaseBottomSheetDialogFragment implements PaymentIView {

    @BindView(R.id.form_list)
    RecyclerView list;

    private final PaymentPresenter<PaymentFragment> presenter = new PaymentPresenter<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_payment;
    }

    @Override
    public void initView(View view) {
        if (getDialog() != null) {
            getDialog().setOnShowListener(dialog -> {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(R.id.design_bottom_sheet);
                if (bottomSheetInternal != null) {
                    BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            });
        }
        ButterKnife.bind(this, view);
        presenter.attachView(this);
    }

    @OnClick({R.id.form_btn})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.form_btn) {

        }
    }

}
