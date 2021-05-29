package kz.yassy.taxi.ui.fragment.service_flow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseFragment;
import kz.yassy.taxi.chat.Chat;
import kz.yassy.taxi.chat.ChatActivity;
import kz.yassy.taxi.common.CancelRequestInterface;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.APIClient;
import kz.yassy.taxi.data.network.model.DataResponse;
import kz.yassy.taxi.data.network.model.Datum;
import kz.yassy.taxi.data.network.model.PastTrip;
import kz.yassy.taxi.data.network.model.Provider;
import kz.yassy.taxi.ui.activity.main.MainActivity;
import kz.yassy.taxi.ui.activity.past_trip_detail.PastTripDetailActivity;
import kz.yassy.taxi.ui.fragment.cancel_ride.CancelRideActivity;

import static kz.yassy.taxi.MvpApplication.DATUM;
import static kz.yassy.taxi.MvpApplication.RIDE_REQUEST;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LONG;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.ESTIMATED_FARE;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LONG;
import static kz.yassy.taxi.common.Constants.Status.ARRIVED;
import static kz.yassy.taxi.common.Constants.Status.PICKED_UP;
import static kz.yassy.taxi.common.Constants.Status.STARTED;

@SuppressLint("NonConstantResourceId")
public class ServiceFlowFragment extends BaseFragment
        implements ServiceFlowIView, CancelRequestInterface {

    @BindView(R.id.source)
    TextView source;
    @BindView(R.id.trip_status_title)
    TextView statusTitle;
    @BindView(R.id.trip_status_desc)
    TextView statusDesc;
    @BindView(R.id.payment_value)
    TextView paymentValue;
    @BindView(R.id.fare_label)
    TextView fareLabel;
    @BindView(R.id.go_btn)
    View goBtn;
    public static String sender = "app";
    @BindView(R.id.chat_btn)
    View chatBtn;
    @BindView(R.id.label_chat_btn)
    TextView labelChatBtn;
    @BindView(R.id.call_btn)
    View callBtn;
    @BindView(R.id.cancel_btn)
    View cancelBtn;
    @BindView(R.id.label_go_btn)
    View labelGoBtn;
    @BindView(R.id.label_call_btn)
    TextView labelCallBtn;
    private Runnable runnable;
    private Handler handler;
    private int delay = 2 * 60 * 1000;
    public int PERMISSIONS_REQUEST_PHONE = 4;
    private boolean isAlreadyReadyClicked = false;
    private String providerPhoneNumber = null;
    private final ServiceFlowPresenter<ServiceFlowFragment> presenter = new ServiceFlowPresenter<>();
    private CancelRequestInterface callback;
    private DatabaseReference myRef;
    private CompositeDisposable mCompositeDisposable;
    private int notificationId = 1200;

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    CountDownTimer mcoundowntimer;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service_flow_last;
    }

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        callback = this;
        presenter.attachView(this);

        mCompositeDisposable = new CompositeDisposable();
        presenter.checkStatus();

        mcoundowntimer = new CountDownTimer(1000000, 1000) {
            @Override
            public void onTick(long l) {
                if ((!isRemoving()) && (renthours != -1)) {
                    if (mdataResponse.getData().getRent_plan() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date dd = sdf.parse(mdataResponse.getData().getCreatedAt());
                            long timestarted = dd.getTime() + renthours * 3600 * 1000;
                            if (System.currentTimeMillis() > timestarted) {
                                // secondSplitUp(0, tvTimer);
                            } else {

                                // secondSplitUp((timestarted - System.currentTimeMillis()) / 1000, tvTimer);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }

            @Override
            public void onFinish() {

            }
        };

        if (getArguments() != null) {
            if (getArguments().getString("show") != null)
                mcoundowntimer.start();

        }


        if (DATUM != null) initView(DATUM);
        return view;
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        mCompositeDisposable.dispose();
        super.onDestroyView();
    }

    @OnClick({R.id.go_btn, R.id.cancel_btn, R.id.call_btn, R.id.chat_btn, R.id.payment_wrapper, R.id.fare_wrapper, R.id.payment_value})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fare_wrapper:
                Intent intent = new Intent(baseActivity(), PastTripDetailActivity.class);
                startActivity(intent);
                break;
            case R.id.go_btn:
                goBtn.setVisibility(View.GONE);
                sendMessage("Уже выхожу");
                labelGoBtn.setVisibility(View.GONE);
                break;
            case R.id.payment_wrapper:

//                PaymentFragment paymentFragment = new PaymentFragment(new User(), true, pos -> {
//
//                });
//                paymentFragment.show(getChildFragmentManager(), PAYMENT);
                break;
            case R.id.cancel_btn:
                startActivity(new Intent(baseActivity(), CancelRideActivity.class));
//                CancelRideDialogFragment cancelRideFragment = new CancelRideDialogFragment(callback);
//                cancelRideFragment.show(baseActivity().getSupportFragmentManager(), cancelRideFragment.getTag());
                break;
            case R.id.call_btn:
                callPhoneNumber(providerPhoneNumber);
                break;
            case R.id.chat_btn:
                if (DATUM != null) {
                    Intent i = new Intent(baseActivity(), ChatActivity.class);
                    i.putExtra("request_id", String.valueOf(DATUM.getId()));
                    startActivity(i);
                }
                break;
        }
    }

    private void sendMessage(String messageStr) {
        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setTimestamp(new Date().getTime());
        chat.setType("text");
        chat.setText(messageStr);
        chat.setRead(0);
        chat.setDriverId(1212);
        chat.setUserId(12121);
        myRef.push().setValue(chat);
        try {
            mCompositeDisposable.add(APIClient
                    .getAPIClient()
                    .postChatItem("app", String.valueOf(DATUM.getCurrentProviderId()), messageStr)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> System.out.println("RRR o = " + o)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void callPhoneNumber(String mobileNumber) {
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            if (ActivityCompat.checkSelfPermission(baseActivity(), Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED)
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber)));
            else ActivityCompat.requestPermissions(baseActivity(),
                    new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE);
        }
    }

    private long timerInHandler = 0L;
    private Handler customHandler = new Handler();

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            // timerInHandler++;
            //secondSplitUp(timerInHandler, tvTimer);
            //customHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_PHONE)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(baseActivity(), "Permission accordée. Réessayer!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelRequestMethod() {
    }


    boolean popupshowed = false;

    @SuppressLint({"StringFormatInvalid", "RestrictedApi", "SetTextI18n"})
    private void initView(Datum datum) {
        providerPhoneNumber = "8" + datum.getProviderNumber().replace(" ", "");
        source.setText(datum.getDAddress());
        paymentValue.setText(datum.getPaymentMode().equals("COMPANY") ? "Бизнес аккаунт" : getString(R.string.cash));
        fareLabel.setText(getString(R.string.trip_fare_label) + " " + RIDE_REQUEST.get(ESTIMATED_FARE) + " ₸");
        presenter.getPastTripDetails(datum.getId());
        switch (datum.getStatus()) {
            case STARTED:
                statusTitle.setText(getString(R.string.trip_title_started, "... минут"));
                goBtn.setVisibility(View.GONE);
                labelGoBtn.setVisibility(View.GONE);
                Log.e(">>>>", "Started");
                statusDesc.setText(datum.getProviderServiceModel() + " " + datum.getProviderServiceNumber());
                break;
            case ARRIVED:
                statusTitle.setText(R.string.trip_title_arrived);
                goBtn.setVisibility(View.VISIBLE);
                labelGoBtn.setVisibility(View.VISIBLE);
                Log.e(">>>>", "Arrived");
                break;
            case PICKED_UP:
                statusTitle.setText("Осталось 15 минут");
                cancelBtn.setVisibility(View.INVISIBLE);
                goBtn.setVisibility(View.GONE);
                labelGoBtn.setVisibility(View.GONE);
                chatBtn.setVisibility(View.GONE);
                labelChatBtn.setVisibility(View.GONE);
                callBtn.setVisibility(View.GONE);
                labelCallBtn.setVisibility(View.GONE);
                statusDesc.setText("По окончанию поездки, пожалуйста не забудьте поставить оценку.");
                break;
            default:
                break;
        }

        if (STARTED.equalsIgnoreCase(datum.getStatus())) {
//            LatLng source = new LatLng(datum.getProvider().getLatitude(), datum.getProvider().getLongitude());
//            LatLng destination = new LatLng(datum.getSLatitude(), datum.getSLongitude());
        } else {
//            LatLng origin = new LatLng(datum.getSLatitude(), datum.getSLongitude());
//            LatLng destination = new LatLng(datum.getDLatitude(), datum.getDLongitude());
//            ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(origin, destination);
        }
        try {

        } catch (Exception e) {
            ((MainActivity) Objects.requireNonNull(getActivity())).deleteAndDrawRoute(new LatLng((double) RIDE_REQUEST.get(SRC_LAT), (double) RIDE_REQUEST.get(SRC_LONG)), new LatLng((double) RIDE_REQUEST.get(DEST_LAT), (double) RIDE_REQUEST.get(DEST_LONG)));
        }
        initChatView(String.valueOf(DATUM.getId()));
    }

    public void secondSplitUp(long biggy, TextView tvTimer) {
        if (biggy < 600) {
            if (!popupshowed) {
                popupshowed = true;
                new AlertDialog.Builder(getContext()).setMessage(R.string.planupgrademsg).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("Continue", (dialogInterface, i) -> {
                    presenter.checkStatus();
                    dialogInterface.dismiss();
                }).show();
            }
            ;

        } else popupshowed = false;
        int hours = (int) biggy / 3600;
        int sec = (int) biggy - hours * 3600;
        int mins = sec / 60;
        sec = sec - mins * 60;
        tvTimer.setText(String.format("%02d:", hours)
                + String.format("%02d:", mins)
                + String.format("%02d", sec));
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        if (mcoundowntimer != null) {
            mcoundowntimer.cancel();
        }
        if (handler != null) handler.removeCallbacks(runnable);
        if (customHandler != null)
            //   customHandler.removeCallbacks(updateTimerThread);
            super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (handler != null) handler.removeCallbacks(runnable);
    }

    private void initChatView(String chatPath) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(chatPath)/*.child(chatPath).child("chat")*/;

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (chat.getSender().equals("provider")) {
//                    sendNotification(chat.getText(), getInstance(), chatPath);
                    Log.e("ChatActivity", "MAinActivity");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    DataResponse mdataResponse;
    int renthours = -1;

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        System.out.println("RRR ServiceFlowFragment.onResume");
        super.onResume();
        handler = new Handler();
        runnable = () -> {
            try {
                if (DATUM.getStatus().equalsIgnoreCase(STARTED)
                        || DATUM.getStatus().equalsIgnoreCase(ARRIVED)) {
//                    src = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
//                    des = SharedHelper.getCurrentLocation(getContext());
//                    Log.e("RRR src = " + src ," dest = " + des);
//                    ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(new LatLng(DATUM.getUser().getLatitude(), DATUM.getUser().getLongitude()),new LatLng(DATUM.getProvider().getLatitude(), DATUM.getProvider().getLongitude()));
                }
                long time = SharedHelper.getKey(getContext(), "timeEstimated", 0);
                if (DATUM.getStatus().equalsIgnoreCase(STARTED)) {
                    statusTitle.setText(getString(R.string.trip_title_started, (time == 0 ? "меньше минуты" : time + " минут")));
                } else if (DATUM.getStatus().equalsIgnoreCase(PICKED_UP)) {
                    statusTitle.setText("Осталось " + (time == 0 ? "меньше минуты" : time + " минут"));
                }
                handler.postDelayed(runnable, 1000);
            } catch (Exception e) {
                handler.postDelayed(runnable, 5000);
                e.printStackTrace();
            }
        };
        handler.postDelayed(runnable, 0);
    }

    @Override
    public void onSuccess(DataResponse dataResponse) {
        mdataResponse = dataResponse;
        if ((dataResponse.getData().getRent_plan() != null) && (dataResponse.getData().getServicerequired().equals("rental"))) {
            renthours = Integer.parseInt(dataResponse.getData().getRent_plan().getHour());
        }

    }

    @Override
    public void onSuccessTripDetails(PastTrip pastTrip) {
        fareLabel.setText(pastTrip.getEstimatedFare() + " ₸");
    }

    @Override
    public void onSuccessProvider(List<Provider> objects) {

    }

    @Override
    public void onErrorProvider(Throwable e) {

    }
}
