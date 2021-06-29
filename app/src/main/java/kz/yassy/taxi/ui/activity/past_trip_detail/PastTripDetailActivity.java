package kz.yassy.taxi.ui.activity.past_trip_detail;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.network.model.PastTrip;
import kz.yassy.taxi.data.network.model.SearchRoute;
import kz.yassy.taxi.ui.fragment.dispute.DisputeCallBack;
import kz.yassy.taxi.ui.fragment.dispute.DisputeFragment;

import static kz.yassy.taxi.MvpApplication.DATUM;

public class PastTripDetailActivity extends BaseActivity implements PastTripDetailsIView, DisputeCallBack {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.s_address)
    TextView sAddress;
    @BindView(R.id.d_address)
    TextView dAddress;
    @BindView(R.id.text_req_type)
    TextView text_type;
    @BindView(R.id.finished_at_time)
    TextView finishedAtTime;
    @BindView(R.id.payment_mode)
    TextView paymentMode;
    @BindView(R.id.payable)
    TextView payable;
    @BindView(R.id.tarif)
    TextView tarif;
    @BindView(R.id.phoneNumber)
    TextView phoneNumber;

    private PastTripDetailsPresenter<PastTripDetailActivity> presenter = new PastTripDetailsPresenter();

    @Override
    public int getLayoutId() {
        return R.layout.activity_past_trip_detail_temp;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        if (DATUM != null) {
            showLoading();
            presenter.getPastTripDetails(DATUM.getId());
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof DisputeFragment) ((DisputeFragment) fragment).setCallBack(this);
    }

    @OnClick({R.id.back_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(PastTrip pastTrip) {
        hideLoading();
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Log.e("PastTrip", pastTrip.toString());
        text_type.setText(pastTrip.getProviderServiceCar() + " " + pastTrip.getProviderServiceNumber());
        sAddress.setText(pastTrip.getsAddress());
        dAddress.setText(pastTrip.getdAddress());
        name.setText(String.format("%s %s", pastTrip.getProviderFirstName(), pastTrip.getProviderLastName()));
        phoneNumber.setText(pastTrip.getProviderNumber());
        tarif.setText(pastTrip.getServiceTypeName().equals("Стандарт") ? "Комфорт" : "Бизнес");
        paymentMode.setText(pastTrip.getPaymentMode().equals("CASH") ? "Наличными" : "Бизнес аккаунт");
        payable.setText(pastTrip.getTotal() + " ₸");
        String strCurrentDate = pastTrip.getAssignedAt();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru"));
        SimpleDateFormat timeFormat;
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("dd MMM yyyy");
        timeFormat = new SimpleDateFormat("hh:mm a");
        String date = format.format(newDate);
        String time = timeFormat.format(newDate);
        finishedAtTime.setText(time);
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
        hideLoading();
    }

    @Override
    public void onSuccessRoute(SearchRoute r) {

    }

    @Override
    public void onRouteError(Throwable throwable) {

    }

    @Override
    public void onDisputeCreated() {
        presenter.getPastTripDetails(DATUM.getId());
    }
}
