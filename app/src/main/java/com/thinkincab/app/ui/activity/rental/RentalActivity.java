package com.thinkincab.app.ui.activity.rental;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseActivity;
import com.thinkincab.app.common.Constants;
import com.thinkincab.app.common.RecyclerTouchListener;
import com.thinkincab.app.data.SharedHelper;
import com.thinkincab.app.data.network.model.EstimateFare;
import com.thinkincab.app.data.network.model.RentalHourPackage;
import com.thinkincab.app.data.network.model.Service;
import com.thinkincab.app.data.network.model.User;
import com.thinkincab.app.ui.activity.location_pick.LocationPickActivity;
import com.thinkincab.app.ui.activity.payment.PaymentActivity;
import com.thinkincab.app.ui.adapter.ServiceAdapterSingle;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.thinkincab.app.MvpApplication.PICK_LOCATION_REQUEST_CODE;
import static com.thinkincab.app.MvpApplication.RIDE_REQUEST;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.PAYMENT_MODE;
import static com.thinkincab.app.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class RentalActivity extends BaseActivity implements RentBookingIView {

    @BindView(R.id.source)
    TextView source;
    @BindView(R.id.rental)
    Spinner rental;
    @BindView(R.id.car_type_rv)
    RecyclerView carTypeRv;
    @BindView(R.id.payment_type)
    TextView paymentType;
    @BindView(R.id.use_wallet)
    CheckBox useWallet;

    ServiceAdapterSingle adapter;
    private RentBookingPresenter<RentalActivity> presenter = new RentBookingPresenter<>();
    HashMap<String, Object> map = new HashMap<>();
    public String pricee;
    @Override
    public int getLayoutId() {
        return R.layout.activity_rental;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        carTypeRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        carTypeRv.setItemAnimator(new DefaultItemAnimator());
        carTypeRv.addOnItemTouchListener(new RecyclerTouchListener(this, carTypeRv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Service service = adapter.getItem(position);
                if (service != null) {
                    ArrayAdapter<RentalHourPackage> userAdapter = new ArrayAdapter<RentalHourPackage>(RentalActivity.this, R.layout.spinner1, service.getRentalHourPackage());
                    rental.setAdapter(userAdapter);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (RIDE_REQUEST.containsKey("s_address")) {
            source.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
        }
       // initPayment(paymentType);
        initPayment2((String) Objects.requireNonNull(RIDE_REQUEST.get(PAYMENT_MODE)), paymentType);
        presenter.services();

    }


    public void onSuccess(List<Service> services) {
        adapter = new ServiceAdapterSingle(this, services);

        carTypeRv.setAdapter(adapter);
        if (services.size() > 0) {
            ArrayAdapter<RentalHourPackage> userAdapter = new ArrayAdapter<>(this, R.layout.spinner1, services.get(0).getRentalHourPackage());
            rental.setAdapter(userAdapter);
        }
    }

    @Override
    public void onSuccessRequest(Object object) {
        finish();
    }

    @Override
    public void onSuccess(EstimateFare estimateFare) {
        map.put("distance", estimateFare.getDistance());
        //map.put("pricing_id", estimateFare.getPricingId());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_instant_ride, null);

        TextView tvPickUp = view.findViewById(R.id.tvPickUp);
        TextView tvDrop = view.findViewById(R.id.tvDrop);
        TextView tvFare = view.findViewById(R.id.tvFare);
        RelativeLayout drop_layout = view.findViewById(R.id.drop_layout);

        tvPickUp.setText(Objects.requireNonNull(String.valueOf(RIDE_REQUEST.get("s_address"))));
        drop_layout.setVisibility(View.GONE);
        tvDrop.setText(Objects.requireNonNull(String.valueOf(RIDE_REQUEST.get("d_address"))));
        pricee = (String.valueOf(estimateFare.getEstimatedFare()));
        tvFare.setText(new StringBuilder().append(SharedHelper.getKey(this, "currency")).append(String.valueOf(estimateFare.getEstimatedFare())).toString());

        map.put("estimated_fare", pricee);
        builder.setView(view);
        AlertDialog mDialog = builder.create();
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        view.findViewById(R.id.tvSubmit).setOnClickListener(view1 -> {
            showLoading();
            presenter.sendRequest(map);
        });

        view.findViewById(R.id.tvCancel).setOnClickListener(view1 -> mDialog.dismiss());
        mDialog.show();
    }


    @OnClick({R.id.source, R.id.get_pricing, R.id.payment_type})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.source:
                Intent sourceIntent = new Intent(this, LocationPickActivity.class);
                sourceIntent.putExtra("isHideDestination", true);
                startActivityForResult(sourceIntent, PICK_LOCATION_REQUEST_CODE);
                break;
            case R.id.get_pricing:
                estimateFare();
                break;
            case R.id.payment_type:
                if (isCCAvenueEnabled)
                    startActivityForResult(new Intent(this, PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
        }
    }

    public static boolean isCCAvenueEnabled = false;
    void estimateFare() {

        map = new HashMap<>(RIDE_REQUEST);

        if (!map.containsKey("s_address")) {
            Toast.makeText(this, getString(R.string.please_enter_address), Toast.LENGTH_SHORT).show();
            return;
        }
        if (adapter == null) {
            Toast.makeText(this, getString(R.string.please_select_car_type), Toast.LENGTH_SHORT).show();
            return;
        }

        if (adapter.getSelectedService() == null) {
            Toast.makeText(this, getString(R.string.please_select_car_type), Toast.LENGTH_SHORT).show();
            return;
        }

        Service service = adapter.getSelectedService();
        if (service.getStatus() != 1) {
            Toast.makeText(this, R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        RentalHourPackage rental = (RentalHourPackage) ((Spinner) findViewById(R.id.rental)).getSelectedItem();
        if (rental == null) {
            Toast.makeText(this, getString(R.string.please_select_rental_type), Toast.LENGTH_SHORT).show();
            return;
        }

        map.put("service_type", ((Service) service).getId());
        map.put("rental_hours", rental.getId());
        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        map.put("service_required", "rental");
        presenter.estimateFare(map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (RIDE_REQUEST.containsKey("s_address")) {
                    source.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
                } else {
                    source.setText("");
                }
            }
        } else if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put("payment_mode", data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put("card_id", data.getStringExtra("card_id"));
                RIDE_REQUEST.put("card_last_four", data.getStringExtra("card_last_four"));
            }
            //initPayment(paymentType);
            initPayment2((String) RIDE_REQUEST.get(PAYMENT_MODE), paymentType);
        }
    }

}
