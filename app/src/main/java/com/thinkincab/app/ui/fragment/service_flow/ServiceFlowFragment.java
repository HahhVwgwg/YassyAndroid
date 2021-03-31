package com.thinkincab.app.ui.fragment.service_flow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseFragment;
import com.thinkincab.app.chat.ChatActivity;
import com.thinkincab.app.common.CancelRequestInterface;
import com.thinkincab.app.data.SharedHelper;
import com.thinkincab.app.data.network.model.DataResponse;
import com.thinkincab.app.data.network.model.Datum;
import com.thinkincab.app.data.network.model.Provider;
import com.thinkincab.app.data.network.model.ProviderService;
import com.thinkincab.app.data.network.model.ServiceType;
import com.thinkincab.app.ui.activity.main.MainActivity;
import com.thinkincab.app.ui.fragment.cancel_ride.CancelRideDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.thinkincab.app.MvpApplication.DATUM;
import static com.thinkincab.app.MvpApplication.RIDE_REQUEST;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.SRC_LAT;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.SRC_LONG;
import static com.thinkincab.app.common.Constants.Status.ARRIVED;
import static com.thinkincab.app.common.Constants.Status.PICKED_UP;
import static com.thinkincab.app.common.Constants.Status.STARTED;

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
    @BindView(R.id.cancel_btn)
    View cancelBtn;
    @BindView(R.id.label_go_btn)
    View labelGoBtn;

    private Runnable runnable;
    private Handler handler;
    private int delay = 2 * 60 * 1000;
    public int PERMISSIONS_REQUEST_PHONE = 4;

    private String providerPhoneNumber = null;
    private final ServiceFlowPresenter<ServiceFlowFragment> presenter = new ServiceFlowPresenter<>();
    private CancelRequestInterface callback;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service_flow;
    }

    CountDownTimer mcoundowntimer;

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        callback = this;
        presenter.attachView(this);

        presenter.checkStatus();

        mcoundowntimer = new CountDownTimer(1000000, 1000) {
            @Override
            public void onTick(long l) {
                if ((!isRemoving()) && (renthours != -1)) {
                    if (mdataResponse.getData().get(0).getRent_plan() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date dd = sdf.parse(mdataResponse.getData().get(0).getCreatedAt());
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
        super.onDestroyView();
    }

    @OnClick({R.id.go_btn, R.id.cancel_btn, R.id.call_btn, R.id.chat_btn, R.id.payment_wrapper, R.id.fare_wrapper})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.go_btn:

                break;
            case R.id.cancel_btn:
                CancelRideDialogFragment cancelRideFragment = new CancelRideDialogFragment(callback);
                cancelRideFragment.show(baseActivity().getSupportFragmentManager(), cancelRideFragment.getTag());
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

    @SuppressLint({"StringFormatInvalid", "RestrictedApi"})
    private void initView(Datum datum) {
        Log.d("TRIP_INFO", "initView trip" + datum.toString());
        Provider provider = datum.getProvider();
        if (provider != null) {
            //firstName.setText(String.format("%s %s", provider.getFirstName(), provider.getLastName()));
            providerPhoneNumber = provider.getMobile();
        }
        source.setText(datum.getDAddress());
        ServiceType serviceType = datum.getServiceType();
        if (serviceType != null) {
            // serviceTypeName.setText(serviceType.getName());
            fareLabel.setText(getString(R.string.trip_fare_label, (int) serviceType.getPrice()));
        }

        ProviderService providerService = datum.getProviderService();
        if (providerService != null) {
            //  serviceNumber.setText(providerService.getServiceNumber());
            statusDesc.setText(providerService.getServiceModel());
        }

        switch (datum.getStatus()) {
            case STARTED:
                statusTitle.setText(getString(R.string.trip_title_started, datum.getOtp()));
                goBtn.setVisibility(View.GONE);
                labelGoBtn.setVisibility(View.GONE);
                break;
            case ARRIVED:
                statusTitle.setText(R.string.trip_title_arrived);
                goBtn.setVisibility(View.VISIBLE);
                labelGoBtn.setVisibility(View.VISIBLE);
                break;
            case PICKED_UP:
                //statusTitle.setText(R.string.you_are_on_ride);
                cancelBtn.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        if (STARTED.equalsIgnoreCase(datum.getStatus())) {
            LatLng source = new LatLng(datum.getProvider().getLatitude(), datum.getProvider().getLongitude());
            LatLng destination = new LatLng(datum.getSLatitude(), datum.getSLongitude());
            ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(source, destination);
        } else {
            LatLng origin = new LatLng(datum.getSLatitude(), datum.getSLongitude());
            LatLng destination = new LatLng(datum.getDLatitude(), datum.getDLongitude());
            ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(origin, destination);
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

    public void secondSplitUp(long biggy, TextView tvTimer) {
        if (biggy < 600) {
            if (!popupshowed) {
                popupshowed = true;
                new AlertDialog.Builder(getContext()).setMessage(R.string.planupgrademsg).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.checkStatus();
                        dialogInterface.dismiss();
                    }
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


    @Override
    public void onResume() {
        System.out.println("RRR ServiceFlowFragment.onResume");
        super.onResume();
        handler = new Handler();
        runnable = () -> {
            try {
                LatLng src = null;
                LatLng des = null;

                if (DATUM.getStatus().equalsIgnoreCase(STARTED)
                        || DATUM.getStatus().equalsIgnoreCase(ARRIVED)) {
                    src = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
                    des = SharedHelper.getCurrentLocation(getContext());
                } else if (DATUM.getStatus().equalsIgnoreCase(PICKED_UP)) {
                    src = SharedHelper.getCurrentLocation(getContext());
                    des = new LatLng(DATUM.getDLatitude(), DATUM.getDLatitude());
                }

                System.out.println("RRR src = " + src + " dest = " + des);

                /*GoogleDirection
                        .withServerKey(getString(R.string.google_map_key))
                        .from(src)
                        .to(des)
                        .transportMode(TransportMode.DRIVING)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(Direction direction, String rawBody) {
                                if (direction.isOK()) {
                                    Route route = direction.getRouteList().get(0);
                                    if (!route.getLegList().isEmpty()) {
                                        Leg leg = route.getLegList().get(0);
                                        providerEta.setVisibility(View.VISIBLE);
                                        String arrivalTime = String.valueOf(leg.getDuration().getText());
                                        if (arrivalTime.contains("hours"))
                                            arrivalTime = arrivalTime.replace("hours", "h\n");
                                        else if (arrivalTime.contains("hour"))
                                            arrivalTime = arrivalTime.replace("hour", "h\n");
                                        if (arrivalTime.contains("mins"))
                                            arrivalTime = arrivalTime.replace("mins", "min");
                                        providerEta.setText(String.format("ETA : %s", arrivalTime));

                                        System.out.println("RRR src ETA = " + String.format("ETA : %s", arrivalTime));
                                    }
                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                t.printStackTrace();
                                System.out.println("RRR ServiceFlowFragment.onDirectionFailure");
                            }
                        });*/
                handler.postDelayed(runnable, delay);
            } catch (Exception e) {
                handler.postDelayed(runnable, 100);
                e.printStackTrace();
            }
        };
        handler.postDelayed(runnable, 100);
    }


    DataResponse mdataResponse;
    int renthours = -1;

    @Override
    public void onSuccess(DataResponse dataResponse) {
        mdataResponse = dataResponse;
        if ((dataResponse.getData().get(0).getRent_plan() != null) && (dataResponse.getData().get(0).getServicerequired().equals("rental"))) {
            renthours = Integer.parseInt(dataResponse.getData().get(0).getRent_plan().getHour());
        }

    }
}
