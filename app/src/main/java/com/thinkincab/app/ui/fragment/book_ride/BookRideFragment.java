package com.thinkincab.app.ui.fragment.book_ride;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseFragment;
import com.thinkincab.app.common.Constants;
import com.thinkincab.app.data.network.APIClient;
import com.thinkincab.app.data.network.model.PromoList;
import com.thinkincab.app.data.network.model.PromoResponse;
import com.thinkincab.app.data.network.model.Service;
import com.thinkincab.app.data.network.model.Tariffs;
import com.thinkincab.app.ui.activity.main.MainActivity;
import com.thinkincab.app.ui.adapter.ServiceAdapter;
import com.thinkincab.app.ui.fragment.payment.PaymentFragment;
import com.thinkincab.app.ui.fragment.schedule.ScheduleFragment;
import com.thinkincab.app.ui.fragment.service.ServiceTypesFragment;
import com.thinkincab.app.ui.utils.DisplayUtils;
import com.thinkincab.app.ui.utils.ListOffset;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.thinkincab.app.MvpApplication.RIDE_REQUEST;
import static com.thinkincab.app.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.CARD_ID;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.CARD_LAST_FOUR;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.DEST_ADD;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.DISTANCE_VAL;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.PAYMENT_MODE;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.SERVICE_TYPE;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.SRC_ADD;
import static com.thinkincab.app.common.Constants.Status.PAYMENT;
import static com.thinkincab.app.common.Constants.Status.SERVICE;
import static com.thinkincab.app.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class BookRideFragment extends BaseFragment implements BookRideIView {

    Unbinder unbinder;
    @BindView(R.id.schedule_ride)
    Button scheduleRide;
    @BindView(R.id.ride_now)
    Button rideNow;
    @BindView(R.id.source)
    TextView source;
    @BindView(R.id.destination)
    TextView destination;
    @BindView(R.id.service_rv)
    RecyclerView serviceRv;
    @BindView(R.id.estimated_payment_mode)
    TextView estimatedPaymentMode;

    private String paymentMode;

    private ServiceAdapter adapter;
    private List<Service> mServices = new ArrayList<>();
    private List<Integer> mPrices = new ArrayList<>();

    private final BookRidePresenter<BookRideFragment> presenter = new BookRidePresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_book_ride;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        presenter.services();
        hideLoading();
        destination.setText((String) RIDE_REQUEST.get(DEST_ADD));
        source.setText((String) RIDE_REQUEST.get(SRC_ADD));
        return view;
    }

    private void setDataOnUi(String serviceName, Service service, Tariffs fare) {
        if (serviceName != null && !serviceName.isEmpty()) {
            try {
                Log.d("sfdgsg", new Gson().toJson(fare));
                RIDE_REQUEST.put(DISTANCE_VAL, fare.getType().get(0).getDistance());
                ((MainActivity) getActivity()).showTime(fare.getType().get(0).getTime());
            } catch (Exception e) {
                Log.d("sfdgsg", e.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    @OnClick({R.id.schedule_ride, R.id.ride_now, R.id.estimated_payment_mode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.schedule_ride:
                ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(new ScheduleFragment());
                break;
            case R.id.ride_now:
                if (Objects.requireNonNull(RIDE_REQUEST.get(PAYMENT_MODE)).toString()
                        .equals(Constants.PaymentMode.CARD)) {
                    if (RIDE_REQUEST.containsKey(CARD_LAST_FOUR))
                        sendRequest();
                    else
                        Toast.makeText(getActivity().getApplicationContext(),
                                getResources().getString(R.string.choose_card), Toast.LENGTH_SHORT)
                                .show();
                } else
                    sendRequest();
                break;
            case R.id.estimated_payment_mode:
                PaymentFragment paymentFragment = new PaymentFragment();
                paymentFragment.show(getChildFragmentManager(), PAYMENT);
                break;
        }
    }

    public void sendRequest() {
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("service_required", "none");
        if (paymentMode != null && !paymentMode.equalsIgnoreCase(""))
            map.put("payment_mode", paymentMode);
        else map.put("payment_mode", "CASH");
        showLoading();
        try {
            presenter.rideNow(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSuccess(@NonNull Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onSuccessCoupon(PromoResponse promoResponse) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onSuccess(List<Service> serviceList) {
        if (serviceList != null && !serviceList.isEmpty()) {
            RIDE_REQUEST.put(SERVICE_TYPE, 1);
            mServices.clear();
            mServices.addAll(serviceList);

            adapter = new ServiceAdapter(mServices, mListener, mEstimateFare, DisplayUtils.getCustomWidth(getActivity(), 2, 60));
            serviceRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            serviceRv.addItemDecoration(new ListOffset(DisplayUtils.dpToPx(24), ListOffset.START));
            serviceRv.setAdapter(adapter);

            if (adapter != null) {
                Service mService = adapter.getSelectedService();
                if (mService != null) RIDE_REQUEST.put(SERVICE_TYPE, mService.getId());
            }
            RIDE_REQUEST.put(SERVICE_TYPE, mServices.get(0).getId());
            estimatedApiCall();
        }
    }

    private Tariffs mEstimateFare;

    private final ServiceTypesFragment.ServiceListener mListener = pos -> {
        if (mPrices.size() == 0) {
            mPrices.add(0);
            mPrices.add(0);
        } else if (mPrices.size() == 1) {
            mPrices.add(0);
        }
        ServiceTypesFragment serviceTypesFragment = ServiceTypesFragment.create(mServices, mPrices, pos);
        serviceTypesFragment.show(getChildFragmentManager(), SERVICE);
    };

    private void estimatedApiCall() {
        Call<Tariffs> call = APIClient.getAPIClient().estimateFare(RIDE_REQUEST);
        call.enqueue(new Callback<Tariffs>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Tariffs> call,
                                   @NonNull Response<Tariffs> response) {
                if (BookRideFragment.this.isVisible()) {
                    if (response.body() != null) {
                        Tariffs estimateFare = response.body();
                        mEstimateFare = estimateFare;
                        adapter.setEstimateFare(estimateFare);
                        mPrices.clear();
                        for (Tariffs.TariffType type : estimateFare.getType()) {
                            mPrices.add(type.getEstimatedFare());
                        }
                        Service service = adapter.getSelectedService();
                        if (service != null) {
                            RIDE_REQUEST.put(SERVICE_TYPE, service.getId());
                            setDataOnUi(service.getName(), service, estimateFare);
                        }

                    } else if (response.raw().code() == 500) try {
                        JSONObject object = new JSONObject(response.errorBody().string());
                        if (object.has("error"))
                            Toast.makeText(baseActivity(), object.optString("error"), Toast.LENGTH_SHORT).show();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Tariffs> call, @NonNull Throwable t) {
                onErrorBase(t);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put(PAYMENT_MODE, data.getStringExtra("payment_mode"));
            paymentMode = data.getStringExtra("payment_mode");

            System.out.println("RRR PAMENT_MODE = " + data.getStringExtra("payment_mode"));

            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put(CARD_ID, data.getStringExtra("card_id"));
                RIDE_REQUEST.put(CARD_LAST_FOUR, data.getStringExtra("card_last_four"));
            }
            initPayment(estimatedPaymentMode);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initPayment(estimatedPaymentMode);
    }

    public interface CouponListener {
        void couponClicked(int pos, PromoList promoList, String promoStatus);
    }
}
