package kz.yassy.taxi.ui.fragment.sos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseBottomSheetDialogFragment;
import kz.yassy.taxi.data.SharedHelper;

import static kz.yassy.taxi.data.SharedHelper.key.SOS_NUMBER;

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

    @OnClick(R.id.form_btn)
    public void onViewClicked() {
        String phone = SharedHelper.getKey(requireContext(), SOS_NUMBER);
        if (phone != null) {
            try {
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + phone)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
