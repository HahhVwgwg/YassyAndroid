package kz.yassy.taxi.ui.activity.past_trip_detail;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.network.model.Datum;
import kz.yassy.taxi.data.network.model.MenuDrawer;
import kz.yassy.taxi.data.network.model.PastTrip;
import kz.yassy.taxi.data.network.model.SearchRoute;
import kz.yassy.taxi.ui.activity.main.DrawerMenuListener;
import kz.yassy.taxi.ui.fragment.dispute.DisputeCallBack;
import kz.yassy.taxi.ui.fragment.dispute.DisputeFragment;
import kz.yassy.taxi.ui.fragment.map.IMapView;
import kz.yassy.taxi.ui.fragment.map.MapFragment;

import static kz.yassy.taxi.MvpApplication.DATUM;

public class PastTripDetailForHistoryActivity extends BaseActivity implements PastTripDetailsIView, DisputeCallBack, IMapView,
        DrawerMenuListener {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.s_address)
    TextView sAddress;
    @BindView(R.id.d_address)
    TextView dAddress;
    @BindView(R.id.text_req_type)
    TextView text_type;
    @BindView(R.id.payment_mode)
    TextView paymentMode;
    @BindView(R.id.payable)
    TextView payable;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.tarif)
    TextView tarif;
    @BindView(R.id.phoneNumber)
    TextView phoneNumber;
    @BindView(R.id.finished_at_time)
    TextView finishedAtTime;

    private Datum datum;
    private MapFragment mapFragment;

    private PastTripDetailsPresenter<PastTripDetailForHistoryActivity> presenter = new PastTripDetailsPresenter();

    @Override
    public int getLayoutId() {
        return R.layout.activity_past_trip_detail_for_history;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ButterKnife.bind(this);
        presenter.attachView(this);
        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        showLoading();
        Log.e("PastTrip", "getPastTrip");
        presenter.getPastTripDetails(getIntent().getExtras().getInt("datumId"));
//        if (DATUM != null) {
//
//        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof DisputeFragment) ((DisputeFragment) fragment).setCallBack(this);
    }

    @OnClick({R.id.back_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
        }
    }


    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onSuccess(PastTrip pastTrip) {
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
        tarif.setText(pastTrip.getServiceTypeName());
        paymentMode.setText(pastTrip.getPaymentMode().equals("CASH") ? "Наличными" : "Бизнес аккаунт");
        payable.setText(pastTrip.getTotal() + " ₸");
        if (pastTrip.getStatus().equals("COMPLETED")) {
            status.setText("Поездка завершена");
        } else {
            switch (pastTrip.getCancelledBy()) {
                case "NONE":
                    status.setText("Поездка завершена");
                    break;
                case "USER":
                    status.setText("Вы отменили");
                    break;
                case "DISPATCHER":
                    status.setText("Отменен диспетчером");
                    break;
                case "PROVIDER":
                    status.setText("Водитель отменил");
                    break;
            }
        }
        presenter.getRoute(pastTrip.getsLatitude(), pastTrip.getsLongitude(), pastTrip.getdLatitude(), pastTrip.getdLongitude());
        String strCurrentDate = pastTrip.getAssignedAt().getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("ru"));
        SimpleDateFormat timeFormat;
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat("dd MMM yyyy");
        timeFormat = new SimpleDateFormat("HH:mm");
        String date = format.format(newDate);
        String time = timeFormat.format(newDate);
        finishedAtTime.setText(time);
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onSuccessRoute(SearchRoute r) {
        mapFragment.showRouteForTaxi(r);
    }

    @Override
    public void onRouteError(Throwable throwable) {

    }

    @Override
    public void onDisputeCreated() {
        presenter.getPastTripDetails(DATUM.getId());
    }

    @Override
    public void onMenuClick(MenuDrawer menu) {

    }

    @Override
    public void onMapMoved(boolean moved) {

    }

    @Override
    public void onActionUp(LatLng point) {

    }

    @Override
    public void onActionDown() {

    }

    @Override
    public void onMapReady() {

    }

    @Override
    public int getMapPadding() {
        return 0;
    }
}
