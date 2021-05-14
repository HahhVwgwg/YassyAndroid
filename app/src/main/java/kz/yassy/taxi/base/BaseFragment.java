package kz.yassy.taxi.base;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Calendar;

import kz.yassy.taxi.MvpApplication;
import kz.yassy.taxi.R;
import kz.yassy.taxi.common.Constants;

public abstract class BaseFragment extends Fragment implements MvpView {

    private View view;
    private BaseActivity mBaseActivity;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(getLayoutId(), container, false);
            initView(view);
        }

        return view;
    }

    protected abstract int getLayoutId();

    protected abstract View initView(View view);

    @Override
    public FragmentActivity baseActivity() {
        return getActivity();
    }

    @Override
    public void showLoading() {
        if (mBaseActivity != null) {
            mBaseActivity.showLoading();
        }
    }

    @Override
    public void hideLoading() {
        if (mBaseActivity != null) {
            mBaseActivity.hideLoading();
        }
    }

    protected void datePicker(DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(baseActivity(), dateSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    protected void hideKeyboard(EditText view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void timePicker(TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        mTimePicker.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            this.mBaseActivity = (BaseActivity) context;
        }
    }

    protected void initPayment(TextView paymentMode) {
        if (MvpApplication.RIDE_REQUEST.containsKey(Constants.RIDE_REQUEST.PAYMENT_MODE))
            switch (MvpApplication.RIDE_REQUEST.get(Constants.RIDE_REQUEST.PAYMENT_MODE).toString()) {
                case Constants.PaymentMode.CASH:
                    paymentMode.setText(getString(R.string.cash));
                    //    paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
                    break;

                //TODO ALLAN - Alterações débito na máquina e voucher
                case Constants.PaymentMode.DEBIT_MACHINE:
                    paymentMode.setText(getString(R.string.debit_machine));
                    //    paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
                    break;

                case Constants.PaymentMode.CARD:
                    if (MvpApplication.RIDE_REQUEST.containsKey(Constants.RIDE_REQUEST.CARD_LAST_FOUR))
                        paymentMode.setText(getString(R.string.card_, MvpApplication.RIDE_REQUEST.get("card_last_four")));
                    else paymentMode.setText(getString(R.string.add_card_));
                    //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card, 0, 0, 0);
                    break;
                case Constants.PaymentMode.PAYPAL:
                    paymentMode.setText(getString(R.string.paypal));
                    //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_paypal, 0, 0, 0);
                    break;
                case Constants.PaymentMode.BRAINTREE:
                    paymentMode.setText(getString(R.string.braintree));
                    break;

                case Constants.PaymentMode.PAYTM:
                    paymentMode.setText(getString(R.string.paytm));
                    break;

                case Constants.PaymentMode.PAYUMONEY:
                    paymentMode.setText(getString(R.string.payumoney));
                    break;

                case Constants.PaymentMode.WALLET:
                    paymentMode.setText(getString(R.string.wallet));
                    break;
            }
        else {
            if (MvpApplication.isCash) {
                MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.PAYMENT_MODE, Constants.PaymentMode.CASH);
                paymentMode.setText(getString(R.string.cash));
                //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
            } else if (MvpApplication.isCard) {
                paymentMode.setText(R.string.add_card_);
                //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card, 0, 0, 0);
                MvpApplication.RIDE_REQUEST.put(Constants.RIDE_REQUEST.PAYMENT_MODE, Constants.PaymentMode.CARD);
            }
        }
    }

    protected void onErrorBase(Throwable t) {
        if (mBaseActivity != null) {
            mBaseActivity.onErrorBase(t);
        }
    }

    protected void handleError(Throwable e) {
        try {
            try {
                hideLoading();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (mBaseActivity != null) {
            mBaseActivity.handleError(e);
        }
    }

    @Override
    public void onSuccessLogout(Object object) {
        if (mBaseActivity != null) {
            mBaseActivity.onSuccessLogout(object);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        if (mBaseActivity != null) {
            mBaseActivity.onError(throwable);
        }
    }

//    protected String getNewNumberFormat(double d) {
//        return BaseActivity.SharedHelper.getKey(this, "currency") + " " +(d);
//    }

}
