package kz.yassy.taxi.ui.fragment.payment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseBottomSheetDialogFragment;
import kz.yassy.taxi.base.KeyboardHeightObserver;
import kz.yassy.taxi.base.KeyboardHeightProvider;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.ui.utils.DisplayUtils;

public class NotesFragment extends BaseBottomSheetDialogFragment implements PaymentIView, KeyboardHeightObserver {

    private final PaymentPresenter<NotesFragment> presenter = new PaymentPresenter<>();
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.form_btn)
    Button formBtn;
    private KeyboardHeightProvider keyboardHeightProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_wishes;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        keyboardHeightProvider = new KeyboardHeightProvider(getActivity());
//        keyboardHeightProvider.addKeyboardListener(height -> {
//            ((ViewGroup.MarginLayoutParams) formBtn.getLayoutParams()).bottomMargin = height + DisplayUtils.dpToPx(60);
//        });
        view.post(() -> keyboardHeightProvider.start());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (keyboardHeightProvider != null) {
            keyboardHeightProvider.setKeyboardHeightObserver(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (keyboardHeightProvider != null) {
            keyboardHeightProvider.setKeyboardHeightObserver(this);
        }
    }

    @OnClick({R.id.form_btn})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.form_btn) {
            SharedHelper.putKey(getContext(), "notes", comment.getText().toString());
            dismiss();
        }
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        String orientationLabel = orientation == Configuration.ORIENTATION_PORTRAIT ? "portrait" : "landscape";
        Log.i("af;ldjfalk;fdja;lf", "MineMine" + height + " " + orientationLabel);
        // color the keyboard height view, this will remain visible when you close the keyboard
        ((ViewGroup.MarginLayoutParams) formBtn.getLayoutParams()).bottomMargin = height + DisplayUtils.dpToPx(30);
        formBtn.requestLayout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        keyboardHeightProvider.close();
    }
}
