package kz.yassy.taxi.ui.fragment.payment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hold1.keyboardheightprovider.KeyboardHeightProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseBottomSheetDialogFragment;
import kz.yassy.taxi.data.SharedHelper;

public class NotesFragment extends BaseBottomSheetDialogFragment implements PaymentIView {

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
//        keyboardHeightProvider = new KeyboardHeightProvider(getActivity());
//        keyboardHeightProvider.addKeyboardListener(height -> {
//            ((ViewGroup.MarginLayoutParams) formBtn.getLayoutParams()).bottomMargin = height + DisplayUtils.dpToPx(60);
//        });

    }

    @OnClick({R.id.form_btn})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.form_btn) {
            SharedHelper.putKey(getContext(), "notes", comment.getText().toString());
            dismiss();
        }
    }

}
