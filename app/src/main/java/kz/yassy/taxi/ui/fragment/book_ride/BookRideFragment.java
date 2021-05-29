package kz.yassy.taxi.ui.fragment.book_ride;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseFragment;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.APIClient;
import kz.yassy.taxi.data.network.model.PromoList;
import kz.yassy.taxi.data.network.model.PromoResponse;
import kz.yassy.taxi.data.network.model.Service;
import kz.yassy.taxi.data.network.model.Tariffs;
import kz.yassy.taxi.data.network.model.User;
import kz.yassy.taxi.ui.activity.main.MainActivity;
import kz.yassy.taxi.ui.adapter.ServiceAdapter;
import kz.yassy.taxi.ui.fragment.payment.NotesFragment;
import kz.yassy.taxi.ui.fragment.payment.PaymentFragment;
import kz.yassy.taxi.ui.fragment.schedule.ScheduleFragment;
import kz.yassy.taxi.ui.fragment.service.ServiceTypesFragment;
import kz.yassy.taxi.ui.utils.DisplayUtils;
import kz.yassy.taxi.ui.utils.ListOffset;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static kz.yassy.taxi.MvpApplication.RIDE_REQUEST;
import static kz.yassy.taxi.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.CARD_ID;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.CARD_LAST_FOUR;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_ADD;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LONG;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DISTANCE_VAL;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.PAYMENT_MODE;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SERVICE_TYPE;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_ADD;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LONG;
import static kz.yassy.taxi.common.Constants.Status.PAYMENT;
import static kz.yassy.taxi.common.Constants.Status.SERVICE;
import static kz.yassy.taxi.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

@SuppressLint("NonConstantResourceId")
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
    private boolean isCash = true;
    private String paymentMode;
    private String noteLocal = "";
    private ServiceAdapter adapter;
    private List<Service> mServices = new ArrayList<>();
    private List<Integer> mPrices = new ArrayList<>();

    private final BookRidePresenter<BookRideFragment> presenter = new BookRidePresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_book_ride;
    }

    private User user;

    private void setDataOnUi(String serviceName, Tariffs fare) {
        if (serviceName != null && !serviceName.isEmpty()) {
            try {
                RIDE_REQUEST.put(DISTANCE_VAL, fare.getType().get(0).getDistance());
                ((MainActivity) getActivity()).showTime(fare.getType().get(0).getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        presenter.services();
        presenter.profile();
        hideLoading();
        Log.e("RIDE_REQUEST", RIDE_REQUEST.toString());
        isCash = SharedHelper.getBoolKey(getContext(), "isSelectedCardIsCash", true);
        estimatedPaymentMode.setText(isCash ? getString(R.string.cash) : "Бизнес аккаунт");
        destination.setText((String) RIDE_REQUEST.get(DEST_ADD));
        source.setText((String) RIDE_REQUEST.get(SRC_ADD));
        SharedHelper.putKey(getContext(), "notes", "");
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(User user) {
        hideLoading();
        this.user = user;
    }

    @OnClick({R.id.schedule_ride, R.id.ride_now, R.id.estimated_payment_mode, R.id.source, R.id.destination, R.id.notes})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.schedule_ride:
                ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(new ScheduleFragment());
                break;
            case R.id.ride_now:
                sendRequest();
                break;
            case R.id.estimated_payment_mode:
                ServiceTypesFragment.ServiceListener listener = pos -> {
                    isCash = pos == 0;
                    estimatedPaymentMode.setText(isCash ? getString(R.string.cash) : "Бизнес аккаунт");
                };
                PaymentFragment paymentFragment = new PaymentFragment(user, isCash, listener);
                paymentFragment.show(getChildFragmentManager(), PAYMENT);
                break;
            case R.id.source:
                ((MainActivity) Objects.requireNonNull(getActivity())).clickToEditTextAndCloseBookMark(0);
                break;
            case R.id.destination:
                ((MainActivity) Objects.requireNonNull(getActivity())).clickToEditTextAndCloseBookMark(1);
                break;
            case R.id.notes:
                NotesFragment notes = new NotesFragment();
                notes.show(getChildFragmentManager(), PAYMENT);
                break;
        }
    }

    @Override
    public void onSuccess(@NonNull Object object) {
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        Log.e("BookRideFragment", ">>.>>>");
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void sendRequest() {
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("service_required", "none");
        if (paymentMode != null && !paymentMode.equalsIgnoreCase(""))
            map.put("payment_mode", paymentMode);
        else map.put("payment_mode", isCash ? "CASH" : "COMPANY");
        map.put("notes", SharedHelper.getKey(getContext(), "notes", ""));
        Log.e("SERVICEINADAPTER", map.toString());
        showLoading();
        try {
            presenter.rideNow(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        Log.e("BookRideServiceList", ">>.>>>");
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

    @Override
    public void onError(Throwable e) {
//        handleError(e);
        hideLoading();
        Log.e("BookRideFragment", e.getMessage());
        ((MainActivity) Objects.requireNonNull(getActivity())).showError(1);
    }

    private void estimatedApiCall() {
        HashMap<String, Object> request = new HashMap<>();
        request.put(DEST_LONG, RIDE_REQUEST.get(DEST_LONG));
        request.put(DEST_LAT, RIDE_REQUEST.get(DEST_LAT));
        request.put(SRC_LONG, RIDE_REQUEST.get(SRC_LONG));
        request.put(SRC_LAT, RIDE_REQUEST.get(SRC_LAT));
        request.put(SRC_ADD, RIDE_REQUEST.get(SRC_ADD));
        request.put(DEST_ADD, RIDE_REQUEST.get(DEST_ADD));
        request.put(SERVICE_TYPE, String.valueOf(RIDE_REQUEST.get(SERVICE_TYPE)));

        Call<Tariffs> call = APIClient.getAPIClient().estimateFare(request);
        call.enqueue(new Callback<Tariffs>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Tariffs> call,
                                   @NonNull Response<Tariffs> response) {
                if (BookRideFragment.this.isVisible()) {
                    if (response.body() != null) {
                        Tariffs estimateFare = response.body();
                        Log.e("estimateFare", estimateFare.toString());
                        mEstimateFare = estimateFare;
                        adapter.setEstimateFare(estimateFare);
                        mPrices.clear();
                        for (Tariffs.TariffType type : estimateFare.getType()) {
                            mPrices.add(type.getEstimatedFare());
                        }
                        Service service = adapter.getSelectedService();
                        Log.e("ServiceMine", service.toString());
                        if (service != null) {
                            RIDE_REQUEST.put(SERVICE_TYPE, service.getId());
                            setDataOnUi(service.getName(), estimateFare);
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
//            initPayment(estimatedPaymentMode);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        initPayment(estimatedPaymentMode);
    }

    private final ServiceTypesFragment.ServiceListener mListener = pos -> {
        System.out.println(pos + " position");
        if (pos == 0) {
            if (mPrices.size() == 0) {
                mPrices.add(0);
                mPrices.add(0);
            } else if (mPrices.size() == 1) {
                mPrices.add(0);
            }
            ServiceTypesFragment serviceTypesFragment = ServiceTypesFragment.create(mServices, mPrices, pos);
            serviceTypesFragment.show(getChildFragmentManager(), SERVICE);
        } else {
            Toast.makeText(getContext(), "Пока этот тариф не доступен", Toast.LENGTH_SHORT).show();
        }
    };

    public interface CouponListener {
        void couponClicked(int pos, PromoList promoList, String promoStatus);
    }
}
