package kz.yassy.taxi.ui.fragment.payment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseBottomSheetDialogFragment;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.model.User;
import kz.yassy.taxi.ui.fragment.service.ServiceTypesFragment;

public class PaymentFragment extends BaseBottomSheetDialogFragment implements PaymentIView {
    @BindView(R.id.business)
    RelativeLayout businessContainer;
    @BindView(R.id.businessTV)
    TextView business;
    @BindView(R.id.businessCheck)
    ImageView businessCheck;
    @BindView(R.id.cashCheck)
    ImageView cashCheck;


    @BindView(R.id.add_payment)
    RelativeLayout addPayment;
    private User user;

    private final PaymentPresenter<PaymentFragment> presenter = new PaymentPresenter<>();
    private boolean isBusiness;
    private ServiceTypesFragment.ServiceListener listener;
    private boolean isCash;


    public PaymentFragment(User user, boolean isCash, ServiceTypesFragment.ServiceListener listener) {
        this.user = user;
        isBusiness = user.getIsBusiness() == 1;
        this.listener = listener;
        this.isCash = isCash;
    }

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
        setSelected(isCash);
        business.setTextColor(isBusiness ? getContext().getResources().getColor(R.color.text_black) : getContext().getResources().getColor(R.color.text_service_grey));
    }

    private void setSelected(boolean isCash) {
        if (isCash) {
            if (businessCheck.getVisibility() == View.VISIBLE) {
                businessCheck.setVisibility(View.GONE);
                cashCheck.setVisibility(View.VISIBLE);
                listener.whenClicked(0);
                SharedHelper.putKey(getContext(), "isSelectedCardIsCash", true);
            }
        } else {
            if (isBusiness) {
                businessCheck.setVisibility(View.VISIBLE);
                cashCheck.setVisibility(View.GONE);
                listener.whenClicked(1);
                SharedHelper.putKey(getContext(), "isSelectedCardIsCash", false);
            }
        }
    }

    @OnClick({R.id.form_btn, R.id.add_payment, R.id.business, R.id.cash, R.id.info, R.id.businessTV})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.form_btn) {
            dismiss();
        } else if (view.getId() == R.id.add_payment) {
//            startActivity(new Intent(getActivity(), AddCardActivityMine.class));
        } else if (view.getId() == R.id.business) {
            setSelected(false);
        } else if (view.getId() == R.id.cash) {
            setSelected(true);
        } else if (view.getId() == R.id.info || view.getId() == R.id.businessTV) {
            new PaymentDetailsBusinessFragment(user).show(getFragmentManager(), null);
        }
    }

}
