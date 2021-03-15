package com.thinkincab.app.ui.activity.main;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.thinkincab.app.BuildConfig;
import com.thinkincab.app.MvpApplication;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseActivity;
import com.thinkincab.app.chat.ChatActivity;
import com.thinkincab.app.common.Constants;
import com.thinkincab.app.common.InfoWindowData;
import com.thinkincab.app.common.LocaleHelper;
import com.thinkincab.app.common.PolyUtil;
import com.thinkincab.app.data.SharedHelper;
import com.thinkincab.app.data.network.model.AddressResponse;
import com.thinkincab.app.data.network.model.DataResponse;
import com.thinkincab.app.data.network.model.LatLngFireBaseDB;
import com.thinkincab.app.data.network.model.MenuDrawer;
import com.thinkincab.app.data.network.model.Message;
import com.thinkincab.app.data.network.model.Provider;
import com.thinkincab.app.data.network.model.SearchAddress;
import com.thinkincab.app.data.network.model.SettingsResponse;
import com.thinkincab.app.data.network.model.User;
import com.thinkincab.app.data.network.model.UserAddress;
import com.thinkincab.app.ui.activity.help.HelpActivity;
import com.thinkincab.app.ui.activity.location_pick.LocationPickActivity;
import com.thinkincab.app.ui.activity.payment.PaymentActivity;
import com.thinkincab.app.ui.activity.setting.SettingsActivity;
import com.thinkincab.app.ui.activity.your_trips.YourTripActivity;
import com.thinkincab.app.ui.adapter.EmptyAddressAdapter;
import com.thinkincab.app.ui.adapter.SearchAddressAdapter;
import com.thinkincab.app.ui.adapter.UserAddressAdapter;
import com.thinkincab.app.ui.fragment.RateCardFragment;
import com.thinkincab.app.ui.fragment.book_ride.BookRideFragment;
import com.thinkincab.app.ui.fragment.invoice.InvoiceFragment;
import com.thinkincab.app.ui.fragment.rate.RatingDialogFragment;
import com.thinkincab.app.ui.fragment.schedule.ScheduleFragment;
import com.thinkincab.app.ui.fragment.searching.SearchingFragment;
import com.thinkincab.app.ui.fragment.service.ServiceTypesFragment;
import com.thinkincab.app.ui.fragment.service_flow.ServiceFlowFragment;
import com.thinkincab.app.ui.fragment.sos.SosFragment;
import com.thinkincab.app.ui.utils.DisplayUtils;
import com.thinkincab.app.ui.utils.KeyboardUtils;
import com.thinkincab.app.ui.utils.ListOffset;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.thinkincab.app.MvpApplication.DATUM;
import static com.thinkincab.app.MvpApplication.RIDE_REQUEST;
import static com.thinkincab.app.MvpApplication.canGoToChatScreen;
import static com.thinkincab.app.MvpApplication.isCard;
import static com.thinkincab.app.MvpApplication.isCash;
import static com.thinkincab.app.MvpApplication.isChatScreenOpen;
import static com.thinkincab.app.MvpApplication.isDebitMachine;
import static com.thinkincab.app.MvpApplication.isVoucher;
import static com.thinkincab.app.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.DEST_ADD;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.DEST_LAT;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.DEST_LONG;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.PAYMENT_MODE;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.SRC_ADD;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.SRC_LAT;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.SRC_LONG;
import static com.thinkincab.app.common.Constants.Status.ARRIVED;
import static com.thinkincab.app.common.Constants.Status.COMPLETED;
import static com.thinkincab.app.common.Constants.Status.DROPPED;
import static com.thinkincab.app.common.Constants.Status.EMPTY;
import static com.thinkincab.app.common.Constants.Status.PICKED_UP;
import static com.thinkincab.app.common.Constants.Status.RATING;
import static com.thinkincab.app.common.Constants.Status.SEARCHING;
import static com.thinkincab.app.common.Constants.Status.SOS;
import static com.thinkincab.app.common.Constants.Status.SERVICE;
import static com.thinkincab.app.common.Constants.Status.STARTED;
import static com.thinkincab.app.data.SharedHelper.key.PROFILE_IMG;
import static com.thinkincab.app.data.SharedHelper.key.SOS_NUMBER;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity implements
        OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener,
        DirectionCallback,
        MainIView,
        LocationListener,
        DrawerMenuListener {

    private static final String TAG = "MainActivity";
    private static String CURRENT_STATUS = EMPTY;
    private final MainPresenter<MainActivity> mainPresenter = new MainPresenter<>();

    @BindView(R.id.llChangeLocation)
    LinearLayout llChangeLocation;
    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.menu_back)
    ImageView menuBack;
    @BindView(R.id.menu_app)
    ImageView menuApp;
    @BindView(R.id.gps)
    ImageView gps;
    @BindView(R.id.source)
    EditText sourceTxt;
    @BindView(R.id.destination)
    EditText destinationTxt;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.top_layout)
    MotionLayout topLayout;
    @BindView(R.id.pick_location_layout)
    LinearLayout pickLocationLayout;
    @BindView(R.id.changeDestination)
    TextView changeDestination;
    @BindView(R.id.btn_work)
    View btnWork;
    @BindView(R.id.btn_home)
    View btnHome;
    @BindView(R.id.erase_src)
    View eraseSrc;
    @BindView(R.id.erase_dest)
    View eraseDest;
    @BindView(R.id.btn_work_value)
    TextView btnWorkValue;
    @BindView(R.id.btn_home_value)
    TextView btnHomeValue;
    @BindView(R.id.addresses)
    RecyclerView addressesList;

    private Boolean isEditable = true;

    private static final Integer serviceMototaxiID = 10;
    private static final Integer serviceMotoboyID = 20;

    private static final long REQUEST_PLACES_DELAY = 1000;
    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {

        }
    };

    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocation;
    private MapFragment mapFragment;
    private DatabaseReference mProviderLocation;

    private InfoWindowData destinationLeg;

    private boolean isDoubleBackPressed = false;
    private boolean isLocationPermissionGranted;
    private boolean canReRoute = true, canCarAnim = true;
    private int getProviderHitCheck;

    private HashMap<Integer, Marker> providersMarker;
    private ArrayList<LatLng> polyLinePoints;
    private Marker srcMarker, destMarker;
    private Polyline mPolyline;
    private LatLng start = null, end = null;
    private Location mLastKnownLocation;

    private DataResponse checkStatusResponse = new DataResponse();
    private UserAddress home = null, work = null;
    private UserAddressAdapter userAddressAdapter;
    private SearchAddressAdapter searchAddressAdapter;

    private DrawerFragment drawerFragment;
    private Runnable r;
    private Handler h;

    private int countRequest = 0;

    private int selectedEditText;

    private final TextWatcher filterTextWatcher = new TextWatcher() {

        private boolean userIsTyping;

        public void afterTextChanged(Editable editable) {
            eraseSrc.setVisibility(sourceTxt.hasFocus() && editable.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            eraseDest.setVisibility(destinationTxt.hasFocus() && editable.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            requestPlacesByDelay(editable);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            userIsTyping = after > count;
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            userIsTyping = before < count;
        }

    };

    @OnFocusChange({R.id.source, R.id.destination})
    public void onFocusChanged(View view, boolean focused) {
        if (view.getId() == R.id.source && focused && sourceTxt.getText() != null && sourceTxt.getText().length() > 0) {
            eraseSrc.setVisibility(View.VISIBLE);
        } else {
            eraseSrc.setVisibility(View.INVISIBLE);
        }
        if (view.getId() == R.id.destination && focused && destinationTxt.getText() != null && destinationTxt.getText().length() > 0) {
            eraseDest.setVisibility(View.VISIBLE);
        } else {
            eraseDest.setVisibility(View.INVISIBLE);
        }
        if (focused) {
            selectedEditText = view.getId();
            if (topLayout.getProgress() == 0) {
                topLayout.enableTransition(R.id.tr, true);
                topLayout.transitionToEnd();
            }
        }
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainPresenter.checkStatus();
        }
    };
    private LatLngBounds.Builder builder;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

        if (Build.VERSION.SDK_INT >= 21)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.bind(this);
        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawer);

        registerReceiver(myReceiver, new IntentFilter(INTENT_FILTER));
        builder = new LatLngBounds.Builder();

        mainPresenter.attachView(this);

        providersMarker = new HashMap<>();

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        h = new Handler();
        r = () -> {

            //TODO ALLAN - Atualiza localização se o endereço estiver nulo
            if (TextUtils.isEmpty(sourceTxt.getText().toString()) || sourceTxt.getText().toString().equals(getText(R.string.pickup_location).toString())) {
                getDeviceLocation();
                displayCurrentAddress();
            }

            if (mLastKnownLocation != null) {
                mainPresenter.checkStatus();
            }

            if (CURRENT_STATUS.equals(SERVICE) || CURRENT_STATUS.equals(EMPTY)) {
                if (mLastKnownLocation != null) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("latitude", mLastKnownLocation.getLatitude());
                    map.put("longitude", mLastKnownLocation.getLongitude());
                    mainPresenter.getProviders(map);
                }
            } else if (CURRENT_STATUS.equals(STARTED) || CURRENT_STATUS.equals(ARRIVED)
                    || CURRENT_STATUS.equals(PICKED_UP)) if (getProviderHitCheck % 3 == 0) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : polyLinePoints) builder.include(latLng);
                LatLngBounds bounds = builder.build();
                try {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
                } catch (Exception e) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
                }
            }
            getProviderHitCheck++;
            h.postDelayed(r, 5000);
        };
        h.postDelayed(r, 5000);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                mainPresenter.getNavigationSettings();
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
        topLayout.addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                if (i == R.id.start) {
                    KeyboardUtils.hideKeyboard(MainActivity.this, sourceTxt, destinationTxt);
                    pickLocationLayout.requestFocus();
                    topLayout.enableTransition(R.id.tr, false);
                    searchAddressAdapter.update(new ArrayList<>());
                    if (RIDE_REQUEST.containsKey(SRC_ADD)
                            && RIDE_REQUEST.containsKey(DEST_ADD)
                            && CURRENT_STATUS.equalsIgnoreCase(EMPTY)) {
                        CURRENT_STATUS = SERVICE;
                        changeFlow(CURRENT_STATUS);
                        LatLng origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
                        LatLng destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
                        drawRoute(origin, destination);
                        motionLayout.setTransition(R.id.tr_to_service);
                        motionLayout.transitionToEnd();
                    }
                    if (RIDE_REQUEST.containsKey(SRC_ADD) && RIDE_REQUEST.get(SRC_ADD) != sourceTxt.getText().toString()) {
                        isEditable = false;
                        sourceTxt.setText((String) RIDE_REQUEST.get(SRC_ADD));
                        isEditable = true;
                    } else if (!RIDE_REQUEST.containsKey(SRC_ADD)) {
                        isEditable = false;
                        sourceTxt.setText("");
                        isEditable = true;
                    }
                    if (RIDE_REQUEST.containsKey(DEST_ADD) && RIDE_REQUEST.get(DEST_ADD) != destinationTxt.getText().toString()) {
                        isEditable = false;
                        destinationTxt.setText((String) RIDE_REQUEST.get(DEST_ADD));
                        isEditable = true;
                    } else if (!RIDE_REQUEST.containsKey(DEST_ADD)) {
                        isEditable = false;
                        destinationTxt.setText("");
                        isEditable = true;
                    }
                } else if (i == R.id.end) {

                } else if (i == R.id.start_service) {
                    motionLayout.setTransition(R.id.tr);
                    CURRENT_STATUS = EMPTY;
                    changeFlow(CURRENT_STATUS);
                    returnToInitial();
                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

            }
        });
        sourceTxt.addTextChangedListener(filterTextWatcher);
        destinationTxt.addTextChangedListener(filterTextWatcher);

        topLayout.enableTransition(R.id.tr, false);
        List<UserAddress> addresses = new ArrayList<>();
        addresses.add(UserAddress.createEmptyHomeAddress());
        addresses.add(UserAddress.createEmptyWorkAddress());
        userAddressAdapter = new UserAddressAdapter(addresses, item -> {

        });
        searchAddressAdapter = new SearchAddressAdapter(new ArrayList<>(), item -> {
            if (selectedEditText == R.id.source) {
                isEditable = false;
                sourceTxt.setText(item.getDisplayName());
                RIDE_REQUEST.put(SRC_ADD, item.getDisplayName());
                RIDE_REQUEST.put(SRC_LONG, item.getLon());
                RIDE_REQUEST.put(SRC_LAT, item.getLat());
                isEditable = true;
                topLayout.transitionToStart();
            } else if (selectedEditText == R.id.destination) {
                isEditable = false;
                destinationTxt.setText(item.getDisplayName());
                RIDE_REQUEST.put(DEST_ADD, item.getDisplayName());
                RIDE_REQUEST.put(DEST_LONG, item.getLon());
                RIDE_REQUEST.put(DEST_LAT, item.getLat());
                isEditable = true;
                topLayout.transitionToStart();
            }
        });
        ConcatAdapter adapter = new ConcatAdapter(userAddressAdapter, new EmptyAddressAdapter(1), searchAddressAdapter);
        addressesList.addItemDecoration(new ListOffset(DisplayUtils.dpToPx(24)));
        addressesList.setAdapter(adapter);
    }

    @Override
    public void onSuccess(DataResponse dataResponse) {
        this.checkStatusResponse = dataResponse;
        updatePaymentEntities();
        SharedHelper.putKey(this, SOS_NUMBER, checkStatusResponse.getSos());
        if (!Objects.requireNonNull(dataResponse.getData()).isEmpty()) {
            if (!CURRENT_STATUS.equals(dataResponse.getData().get(0).getStatus())) {
                DATUM = dataResponse.getData().get(0);
                CURRENT_STATUS = DATUM.getStatus();
                changeFlow(CURRENT_STATUS);
            }
        } else if (CURRENT_STATUS.equals(SERVICE))
            System.out.println("RRR CURRENT_STATUS = " + CURRENT_STATUS);
        else {
            CURRENT_STATUS = EMPTY;
            changeFlow(CURRENT_STATUS);
        }

        if (CURRENT_STATUS.equals(STARTED)
                || CURRENT_STATUS.equals(ARRIVED)
                || CURRENT_STATUS.equals(PICKED_UP))
            if (mProviderLocation == null) {
                mProviderLocation = FirebaseDatabase.getInstance().getReference()
                        .child("loc_p_" + DATUM.getProvider().getId());
                updateDriverNavigation();
            }

        if (canGoToChatScreen) {
            if (!isChatScreenOpen && DATUM != null) {
                Intent i = new Intent(baseActivity(), ChatActivity.class);
                i.putExtra("request_id", String.valueOf(DATUM.getId()));
                startActivity(i);
            }
            canGoToChatScreen = false;
        }
    }


    public void changeFlow(String status) {
        System.out.println("RRR CURRENT_STATUS = " + status);

        dismissDialog(SEARCHING);
        dismissDialog(RATING);
        switch (status) {
            case EMPTY:
                CURRENT_STATUS = EMPTY;
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.nav_view));
                menuApp.setVisibility(View.VISIBLE);
                menuBack.setVisibility(View.GONE);
                pickLocationLayout.setVisibility(View.VISIBLE);
                mGoogleMap.clear();
                providersMarker.clear();
                hideLoading();
                addAllProviders(SharedHelper.getProviders(this));
                changeFragment(null);
                btnHomeValue.setText(home != null ? R.string.home : R.string.add_home);
                btnWorkValue.setText(work != null ? R.string.work : R.string.add_work);
                mProviderLocation = null;
                polyLinePoints = null;
                break;
            case SERVICE:
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view));
                menuApp.setVisibility(View.GONE);
                menuBack.setVisibility(View.VISIBLE);
                updatePaymentEntities();
                changeFragment(new BookRideFragment());
                break;
            case SEARCHING:
                pickLocationLayout.setVisibility(View.GONE);
                updatePaymentEntities();
                SearchingFragment searchingFragment = new SearchingFragment();
                searchingFragment.show(getSupportFragmentManager(), SEARCHING);
                break;
            case STARTED:
                mGoogleMap.clear();
                pickLocationLayout.setVisibility(View.GONE);
                menuBack.setVisibility(View.GONE);
                if (DATUM != null)
                    FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(DATUM.getId()));
                changeFragment(new ServiceFlowFragment());
                break;
            case ARRIVED:
                pickLocationLayout.setVisibility(View.GONE);
                changeFragment(new ServiceFlowFragment());
                break;
            case PICKED_UP:
                pickLocationLayout.setVisibility(View.GONE);

                if ((DATUM.getServicerequired().equals("rental") || (DATUM.getServicerequired().equals("outstation")))) {
                } else llChangeLocation.setVisibility(View.VISIBLE);
                changeDestination.setText(DATUM.getDAddress());

                ServiceFlowFragment flow = new ServiceFlowFragment();
                Bundle bn = new Bundle();
                bn.putString("show", "yes");
                flow.setArguments(bn);
                changeFragment(flow);
                break;
            case DROPPED:
            case COMPLETED:
                pickLocationLayout.setVisibility(View.GONE);
                llChangeLocation.setVisibility(View.GONE);
                changeFragment(new InvoiceFragment());
                break;
            case RATING:
                changeFragment(null);
                if (DATUM != null)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
                new RatingDialogFragment().show(getSupportFragmentManager(), RATING);
                RIDE_REQUEST.clear();
                mGoogleMap.clear();
                pickLocationLayout.setVisibility(View.GONE);
                sourceTxt.setText("");
                sourceTxt.setHint(getString(R.string.fetching_current_location));
                destinationTxt.setText("");
                break;
            default:
                break;
        }
    }


    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            showLoading();
            HashMap<String, Object> cardHashMap = new HashMap<>();
            cardHashMap.put("request_id", DATUM.getId());
            cardHashMap.put("tips", "0");
            cardHashMap.put("payment_type", "ONLINE");
            cardHashMap.put("transaction_code", razorpayPaymentID);
            mainPresenter.payment(cardHashMap);

        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mainPresenter.getNavigationSettings();
        registerReceiver(myReceiver, new IntentFilter(INTENT_FILTER));
        mapFragment.onResume();
        mainPresenter.getUserInfo();
        mainPresenter.checkStatus();
        if (CURRENT_STATUS.equalsIgnoreCase(EMPTY)) {
            RIDE_REQUEST.remove(DEST_ADD);
            RIDE_REQUEST.remove(DEST_LAT);
            RIDE_REQUEST.remove(DEST_LONG);
            mainPresenter.getSavedAddress();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mapFragment.onDestroy();
        mainPresenter.onDetach();
        unregisterReceiver(myReceiver);
        if (r != null) h.removeCallbacks(r);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (getSupportFragmentManager().findFragmentById(R.id.container)
                    instanceof ServiceFlowFragment) {
                getSupportFragmentManager().popBackStack();
            }
            getSupportFragmentManager().popBackStack();
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                mainPresenter.checkStatus();
                topLayout.transitionToStart();
            }
        } else {
            if (isDoubleBackPressed) {
                super.onBackPressed();
                return;
            }
            this.isDoubleBackPressed = true;
            Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(() -> isDoubleBackPressed = false, 2000);
    }

    private void returnToInitial() {
        destinationTxt.setText("");
        RIDE_REQUEST.remove(DEST_ADD);
        RIDE_REQUEST.remove(DEST_LAT);
        RIDE_REQUEST.remove(DEST_LONG);
    }

    public void ShowLogoutPopUp() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder
                .setMessage(getString(R.string.are_sure_you_want_to_logout)).setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> mainPresenter.logout(SharedHelper.getKey(this, "user_id")))
                .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void requestPlacesByDelay(Editable s) {
        timer.cancel();
        timerTask.cancel();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (isEditable && !s.toString().trim().isEmpty()) {
                        mainPresenter.startSearch(s.toString());
                    }
                });
            }
        };
        timer.schedule(timerTask, REQUEST_PLACES_DELAY);
    }

    private void alertBecomeDriver() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.DRIVER_PACKAGE));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onCameraIdle() {

    }

    @OnClick({R.id.sos, R.id.erase_src, R.id.erase_dest, R.id.btn_home, R.id.btn_work, R.id.menu_app, R.id.gps, R.id.source, R.id.destination, R.id.changeDestination, R.id.menu_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu_app:
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else {
                    User user = new Gson().fromJson(SharedHelper.getKey(this, "userInfo"), User.class);
                    if (user != null) {
                        drawerFragment.updateUser(user);
                        SharedHelper.putKey(MainActivity.this, PROFILE_IMG, user.getPicture());
                    }
                    drawerLayout.openDrawer(GravityCompat.START);
                }

                break;
            case R.id.sos:
                SosFragment sosFragment = new SosFragment();
                sosFragment.show(getSupportFragmentManager(), SOS);
                break;
            case R.id.menu_back:
                topLayout.transitionToStart();
                break;
            case R.id.erase_src:
                sourceTxt.setText("");
                eraseSrc.setVisibility(View.INVISIBLE);
                RIDE_REQUEST.remove(SRC_ADD);
                RIDE_REQUEST.remove(SRC_LAT);
                RIDE_REQUEST.remove(SRC_LONG);
                break;
            case R.id.erase_dest:
                destinationTxt.setText("");
                eraseDest.setVisibility(View.INVISIBLE);
                RIDE_REQUEST.remove(DEST_ADD);
                RIDE_REQUEST.remove(DEST_LAT);
                RIDE_REQUEST.remove(DEST_LONG);
                break;
            case R.id.gps:
                if (mLastKnownLocation != null) {
                    LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
                    displayCurrentAddress();
                }
                break;
            case R.id.source:
                CURRENT_STATUS = EMPTY;
                if (topLayout.getProgress() == 0) {
                    topLayout.enableTransition(R.id.tr, true);
                    topLayout.transitionToEnd();
                }
                //Intent sourceIntent = new Intent(this, LocationPickActivity.class);
                //sourceIntent.putExtra("actionName", Constants.LocationActions.SELECT_SOURCE);
                //startActivityForResult(sourceIntent, REQUEST_PICK_LOCATION);
                break;
            case R.id.destination:
                CURRENT_STATUS = EMPTY;
                if (topLayout.getProgress() == 0) {
                    topLayout.enableTransition(R.id.tr, true);
                    topLayout.transitionToEnd();
                }
                //Intent intent = new Intent(this, LocationPickActivity.class);
                //intent.putExtra("actionName", Constants.LocationActions.SELECT_DESTINATION);
                //startActivityForResult(intent, REQUEST_PICK_LOCATION);
                break;
            case R.id.changeDestination:
                Intent destIntent = new Intent(this, LocationPickActivity.class);
                destIntent.putExtra("actionName", Constants.LocationActions.CHANGE_DESTINATION);
                startActivityForResult(destIntent, REQUEST_PICK_LOCATION);
                break;
            case R.id.btn_home:
                if (home != null) {
                    updateSavedAddress(home);
                } else {

                }
                break;
            case R.id.btn_work:
                if (work != null) {
                    updateSavedAddress(work);
                } else {

                }
                break;
        }
    }

    private void updateSavedAddress(UserAddress userAddress) {
        RIDE_REQUEST.put(DEST_ADD, userAddress.getAddress());
        RIDE_REQUEST.put(DEST_LAT, userAddress.getLatitude());
        RIDE_REQUEST.put(DEST_LONG, userAddress.getLongitude());
        destinationTxt.setText(String.valueOf(RIDE_REQUEST.get(DEST_ADD)));

        if (RIDE_REQUEST.containsKey(SRC_ADD) && RIDE_REQUEST.containsKey(DEST_ADD)) {
            LatLng origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
            LatLng destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
            drawRoute(origin, destination);
            CURRENT_STATUS = SERVICE;
            changeFlow(CURRENT_STATUS);
        }
    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        this.mGoogleMap = googleMap;

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        displayCurrentAddress();
    }

    private void updateDriverNavigation() {
        System.out.println("RRR called : updateDriverNavigation :: ");
        if (mProviderLocation != null)
            mProviderLocation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        LatLngFireBaseDB fbData = dataSnapshot.getValue(LatLngFireBaseDB.class);
                        assert fbData != null;
                        double lat = fbData.getLat();
                        double lng = fbData.getLng();

                        System.out.println("RRR Lat FIREBASE: " + lat + " Lng: " + lng);

                        if (lat != 0.00 && lng != 0.00) {
                            if (STARTED.equalsIgnoreCase(CURRENT_STATUS) ||
                                    ARRIVED.equalsIgnoreCase(CURRENT_STATUS) ||
                                    PICKED_UP.equalsIgnoreCase(CURRENT_STATUS)) {
                                LatLng source = null, destination = null;
                                switch (CURRENT_STATUS) {
                                    case STARTED:
                                        source = new LatLng(lat, lng);
                                        destination = new LatLng(DATUM.getSLatitude(), DATUM.getSLongitude());
                                        break;
                                    case ARRIVED:
                                    case PICKED_UP:
                                        source = new LatLng(lat, lng);
                                        destination = new LatLng(DATUM.getDLatitude(), DATUM.getDLongitude());
                                        break;
                                }
                                if (polyLinePoints == null || polyLinePoints.size() < 2 || mPolyline == null)
                                    drawRoute(source, destination);
                                else {
                                    int index = checkForReRoute(source);
                                    if (index < 0) reRoutingDelay(source, destination);
                                    else polyLineRerouting(source, index);
                                }

                                if (start != null) {
                                    SharedHelper.putCurrentLocation(MainActivity.this, start);
                                    end = start;
                                }
                                start = source;
                                if (end != null && canCarAnim) {
                                    if (start != null) {
                                        System.out.println("CAR ANIM ===>>");
                                        carAnim(srcMarker, end, start);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("RRR ", "Failed to read value.", error.toException());
                }
            });
    }

    private void reRoutingDelay(LatLng source, LatLng destination) {
        if (canReRoute) {
            canReRoute = !canReRoute;
            drawRoute(source, destination);
            new Handler().postDelayed(() -> canReRoute = true, 8000);
        }
    }

    private void polyLineRerouting(LatLng point, int index) {
        if (index > 0) {
            polyLinePoints.subList(0, index + 1).clear();
            polyLinePoints.add(0, point);
            mPolyline.remove();

            mPolyline = mGoogleMap.addPolyline(DirectionConverter.createPolyline
                    (this, polyLinePoints, 5, getResources().getColor(R.color.colorAccent)));

            System.out.println("RRR mPolyline = " + polyLinePoints.size());
        } else System.out.println("RRR mPolyline = Failed");
    }

    private int checkForReRoute(LatLng point) {
        if (polyLinePoints != null && polyLinePoints.size() > 0) {
            System.out.println("RRR indexOnEdgeOrPath = " +
                    new PolyUtil().indexOnEdgeOrPath(point, polyLinePoints, false, true, 100));
            //      indexOnEdgeOrPath returns -1 if the point is outside the polyline
            //      returns the index position if the point is inside the polyline
            return new PolyUtil().indexOnEdgeOrPath(point, polyLinePoints, false, true, 100);
        } else return -1;
    }

    public void drawRoute(LatLng source, LatLng destination) {
/*        GoogleDirection
                .withServerKey(getString(R.string.google_map_key))
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);*/
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        if (direction.isOK()) {
            if (!CURRENT_STATUS.equalsIgnoreCase(SERVICE))
                mGoogleMap.clear();
            Route route = direction.getRouteList().get(0);
            if (!route.getLegList().isEmpty()) {

                Leg leg = route.getLegList().get(0);
                InfoWindowData originLeg = new InfoWindowData();
                originLeg.setAddress(leg.getStartAddress());
                originLeg.setArrival_time(null);
                originLeg.setDistance(leg.getDistance().getText());

                destinationLeg = new InfoWindowData();
                destinationLeg.setAddress(leg.getEndAddress());
                destinationLeg.setArrival_time(leg.getDuration().getText());
                destinationLeg.setDistance(leg.getDistance().getText());

                LatLng origin = new LatLng(leg.getStartLocation().getLatitude(), leg.getStartLocation().getLongitude());
                LatLng destination = new LatLng(leg.getEndLocation().getLatitude(), leg.getEndLocation().getLongitude());

                //SETA TIPO DE ICONES
                int serviceIcon;
                if (DATUM != null) {

                    if (DATUM.getServiceTypeId() == serviceMototaxiID) {
                        //Se for mototaxi
                        serviceIcon = R.drawable.car_icon_11;
                    } else if (DATUM.getServiceTypeId() == serviceMotoboyID) {
                        //Se for motoboy
                        serviceIcon = R.drawable.car_icon_11;
                    } else {
                        //Se for outro (carro)
                        serviceIcon = R.drawable.car_icon_11;
                    }
                } else {
                    serviceIcon = R.drawable.car_icon_11;
                }


                if (CURRENT_STATUS.equalsIgnoreCase(SERVICE))
                    srcMarker = mGoogleMap.addMarker(new MarkerOptions()

                            .position(origin)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView())));
                else srcMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(origin)
                        .icon(BitmapDescriptorFactory.fromResource(serviceIcon)));

                if (destMarker != null) destMarker.remove();
                destMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.des_icon))
                        .position(destination));
            }

            polyLinePoints = route.getLegList().get(0).getDirectionPoint();

            if (CURRENT_STATUS.equalsIgnoreCase(SERVICE)) {
                if (mPolyline != null) mPolyline.remove();
                mPolyline = mGoogleMap.addPolyline(DirectionConverter.createPolyline
                        (this, polyLinePoints, 5, getResources().getColor(R.color.colorAccent)));
                LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
                LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
                LatLngBounds bounds = new LatLngBounds(southwest, northeast);
                try {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
                } catch (Exception e) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
                }
            } else
                mPolyline = mGoogleMap.addPolyline(DirectionConverter.createPolyline
                        (this, polyLinePoints, 2, getResources().getColor(R.color.colorAccent)));
        } else {
            changeFlow(EMPTY);

            Toast.makeText(this, getString(R.string.root_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    private void carAnim(final Marker marker, final LatLng start, final LatLng end) {
        System.out.println("RRR MainActivity.carAnim");
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(1900);
        final LatLngInterface latLngInterpolator = new LatLngInterface.LinearFixed();
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(valueAnimator1 -> {
            canCarAnim = false;
            float v = valueAnimator1.getAnimatedFraction();
            LatLng newPos = latLngInterpolator.interpolate(v, start, end);
            marker.setPosition(newPos);
            marker.setAnchor(0.5f, 0.5f);
            marker.setRotation(bearingBetweenLocations(start, end));
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                canCarAnim = true;
            }
        });
        animator.start();
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        System.out.println("RRR onDirectionFailure = [" + t.getMessage() + "]");
        t.printStackTrace();
    }

    public void changeFragment(Fragment fragment) {
        if (isFinishing()) return;

        if (fragment != null) {
            if (fragment instanceof BookRideFragment) {

            } else if (fragment instanceof ServiceTypesFragment ||
                    fragment instanceof ServiceFlowFragment || fragment instanceof RateCardFragment)
                container.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            else container.setBackgroundColor(getResources().getColor(R.color.white));

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (fragment instanceof RateCardFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ScheduleFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ServiceTypesFragment) {
                fragmentTransaction.addToBackStack(fragment.getTag());

            } else if (fragment instanceof BookRideFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());

            try {
                fragmentTransaction.replace(R.id.container, fragment, fragment.getTag());
                fragmentTransaction.commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            for (Fragment fragmentd : getSupportFragmentManager().getFragments()) {
                if (fragmentd instanceof ServiceFlowFragment) {
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
                }
                if (fragmentd instanceof InvoiceFragment)
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
            }
            container.removeAllViews();
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    void dismissDialog(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof SearchingFragment) {
            SearchingFragment df = (SearchingFragment) fragment;
            df.dismissAllowingStateLoss();
        } else if (fragment instanceof RatingDialogFragment) {
            RatingDialogFragment df = (RatingDialogFragment) fragment;
            df.dismissAllowingStateLoss();
        }
    }

    private Bitmap getMarkerBitmapFromView() {

        View mView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.map_custom_infowindow, null);

        TextView tvEtaVal = mView.findViewById(R.id.tvEstimatedFare);
        String arrivalTime = destinationLeg.getArrival_time();
        if (arrivalTime.contains("hours")) arrivalTime = arrivalTime.replace("hours", "h\n");
        else if (arrivalTime.contains("hour")) arrivalTime = arrivalTime.replace("hour", "h\n");
        if (arrivalTime.contains("mins")) arrivalTime = arrivalTime.replace("mins", "min");
        tvEtaVal.setText(arrivalTime);
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
        mView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(mView.getMeasuredWidth(),
                mView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = mView.getBackground();
        if (drawable != null) drawable.draw(canvas);
        mView.draw(canvas);
        return returnedBitmap;
    }

    void getDeviceLocation() {
        try {
            if (isLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocation.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                ), DEFAULT_ZOOM));

                        SharedHelper.putKey(this, "latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                        SharedHelper.putKey(this, "longitude", String.valueOf(mLastKnownLocation.getLongitude()));
                    } else {
                        mDefaultLocation = new LatLng(
                                Double.valueOf(SharedHelper.getKey(this, "latitude", "-33.8523341")),
                                Double.valueOf(SharedHelper.getKey(this, "longitude", "151.2106085"))
                        );
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) isLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (isLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.getUiSettings().setCompassEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        isLocationPermissionGranted = false;
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
                updateLocationUI();
                getDeviceLocation();
                displayCurrentAddress();
            }
    }

    @Override
    public void onSuccess(@NonNull User user) {
        String dd = LocaleHelper.getLanguage(this);
        String userLanguage = (user.getLanguage() == null) ? Constants.Language.ENGLISH : user.getLanguage();
        if (!userLanguage.equalsIgnoreCase(dd)) {
            LocaleHelper.setLocale(getApplicationContext(), user.getLanguage());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        SharedHelper.putKey(this, "lang", user.getLanguage());
        SharedHelper.putKey(this, "stripe_publishable_key", user.getStripePublishableKey());
        SharedHelper.putKey(this, "currency", user.getCurrency());
        SharedHelper.putKey(this, "currency_code", user.getCurrency());
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        SharedHelper.putKey(this, "walletBalance", String.valueOf(user.getWalletBalance()));
        SharedHelper.putKey(this, "userInfo", printJSON(user));

        SharedHelper.putKey(this, "referral_code", user.getReferral_unique_id());
        SharedHelper.putKey(this, "referral_count", user.getReferral_count());
        SharedHelper.putKey(this, "referral_text", user.getReferral_text());
        SharedHelper.putKey(this, "referral_total_text", user.getReferral_total_text());

        drawerFragment.updateUser(user);
        SharedHelper.putKey(MainActivity.this, PROFILE_IMG, user.getPicture());

        MvpApplication.showOTP = user.getRide_otp().equals("1");
    }

    @Override
    public void onSuccessLogout(Object object) {
        LogoutApp();
    }

    @Override
    public void onSuccess(AddressResponse response) {
        home = (response.getHome().isEmpty()) ? null : response.getHome().get(response.getHome().size() - 1);
        work = (response.getWork().isEmpty()) ? null : response.getWork().get(response.getWork().size() - 1);
        if (userAddressAdapter != null) {
            List<UserAddress> addresses = new ArrayList<>();
            addresses.add(home != null ? home : UserAddress.createEmptyHomeAddress());
            addresses.add(work != null ? work : UserAddress.createEmptyWorkAddress());
            userAddressAdapter.update(addresses);
        }
        btnHomeValue.setText(home != null ? R.string.home : R.string.add_home);
        btnWorkValue.setText(work != null ? R.string.work : R.string.add_work);
    }

    @Override
    public void onSuccess(List<Provider> providerList) {
        SharedHelper.putProviders(this, printJSON(providerList));
    }

    private void addAllProviders(List<Provider> providers) {
        if (providers != null) for (Provider provider : providers)
            if (providersMarker.get(provider.getId()) != null) {
                Marker marker = providersMarker.get(provider.getId());
                LatLng startPos = marker.getPosition();
                LatLng endPos = new LatLng(provider.getLatitude(), provider.getLongitude());
                marker.setPosition(endPos);
                carAnim(marker, startPos, endPos);
            } else {
                //SETA TIPO DE ICONES
                int serviceIcon;
                if (provider.getProviderService().getServiceTypeId() == serviceMototaxiID) {
                    //Se for mototaxi
                    serviceIcon = R.drawable.car_icon_11;
                } else if (provider.getProviderService().getServiceTypeId() == serviceMotoboyID) {
                    //Se for motoboy
                    serviceIcon = R.drawable.car_icon_11;
                } else {
                    //Se for outro (carro)
                    serviceIcon = R.drawable.car_icon_11;
                }

                new TheTask(provider.getId(), provider.getLatitude(), provider.getLongitude(), provider.getFirstName(), "", MvpApplication.marker).execute();

                MarkerOptions markerOptions = new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                        .rotation(0.0f)
                        .snippet("" + provider.getId())
                        .icon(BitmapDescriptorFactory.fromResource(serviceIcon));
                providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
            }
    }

    @Override
    public void onMenuClick(MenuDrawer menu) {
        if (menu instanceof MenuDrawer.MenuMain) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (menu instanceof MenuDrawer.MenuPayments) {
            startActivity(new Intent(this, PaymentActivity.class));
        } else if (menu instanceof MenuDrawer.MenuHistory) {
            startActivity(new Intent(this, YourTripActivity.class));
        } else if (menu instanceof MenuDrawer.MenuSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (menu instanceof MenuDrawer.MenuHelp) {
            startActivity(new Intent(this, HelpActivity.class));
        } else if (menu instanceof MenuDrawer.MenuDriver) {
            alertBecomeDriver();
        } else if (menu instanceof MenuDrawer.MenuLogout) {
            ShowLogoutPopUp();
        }
    }

    public class TheTask extends AsyncTask<Void, Void, Void> {
        double lat, lang;
        int pos;
        String firstname, dis, markerurl;
        Bitmap bmp;

        public TheTask(int i, Double latitude, Double longitude, String firstName, String description, String marker) {

            firstname = firstName;
            lat = latitude;
            lang = longitude;
            dis = description;
            markerurl = marker;
            pos = i;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... params) {
            URL url;
            try {
                url = new URL(markerurl);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                bmp.setPixel(18, 18, getResources().getColor(R.color.default_dot_color));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);

            if (bmp != null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat, lang))
                        .anchor(0.5f, 0.5f)
                        .title(firstname)
                        .snippet(dis)
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp));

                builder.include(markerOptions.getPosition());
                providersMarker.put(pos, mGoogleMap.addMarker(markerOptions));

            } else {
                int serviceIcon;
                serviceIcon = R.drawable.car_icon_11;

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(lat, lang))
                        .anchor(0.5f, 0.5f)
                        .title(firstname)
                        .snippet(dis)
                        .icon(BitmapDescriptorFactory.fromResource(serviceIcon));

                builder.include(markerOptions.getPosition());

                providersMarker.put(pos, mGoogleMap.addMarker(markerOptions));


            }

            //   providersMarker.put(pos, mGoogleMap.addMarker(markerOptions));

        }
    }

    public void addSpecificProviders(List<Provider> providers, String key) {
        if (providers != null) {
            Bitmap b;
            BitmapDescriptor bd;
            try {
                b = Bitmap.createScaledBitmap(decodeBase64(SharedHelper.getKey
                        (this, key)), 60, 60, false);
                bd = BitmapDescriptorFactory.fromBitmap(b);
            } catch (Exception e) {

                bd = BitmapDescriptorFactory.fromResource(R.drawable.car_icon_11);
                e.printStackTrace();
            }
            Iterator<Map.Entry<Integer, Marker>> it = providersMarker.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Marker> pair = it.next();
                Marker marker = providersMarker.get(pair.getKey());
                marker.remove();
                it.remove();
            }
            for (Provider provider : providers) {

                //SETA TIPO DE ICONES
                int serviceIcon;
                if (provider.getProviderService().getServiceTypeId() == serviceMototaxiID) {
                    //Se for mototaxi
                    serviceIcon = R.drawable.car_icon_11;
                } else if (provider.getProviderService().getServiceTypeId() == serviceMotoboyID) {
                    //Se for motoboy
                    serviceIcon = R.drawable.car_icon_11;
                } else {
                    //Se for outro (carro)
                    serviceIcon = R.drawable.car_icon_11;
                }

                new TheTask(provider.getId(), provider.getLatitude(), provider.getLongitude(), provider.getFirstName(), "", MvpApplication.marker).execute();
                MarkerOptions markerOptions = new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                        .rotation(0.0f)
                        .snippet("" + provider.getId())
                        .icon(BitmapDescriptorFactory.fromResource(serviceIcon));
                providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
            }
        }
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments())
            fragment.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_LOCATION) if (resultCode == Activity.RESULT_OK) {
            if (RIDE_REQUEST.containsKey(SRC_ADD))
                sourceTxt.setText(String.valueOf(RIDE_REQUEST.get(SRC_ADD)));
            else sourceTxt.setText("");
            if (RIDE_REQUEST.containsKey(DEST_ADD))
                destinationTxt.setText(String.valueOf(RIDE_REQUEST.get(DEST_ADD)));
            else destinationTxt.setText("");

            if (RIDE_REQUEST.containsKey(SRC_ADD)
                    && RIDE_REQUEST.containsKey(DEST_ADD)
                    && CURRENT_STATUS.equalsIgnoreCase(EMPTY)) {
                CURRENT_STATUS = SERVICE;
                changeFlow(CURRENT_STATUS);
                LatLng origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
                LatLng destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
                drawRoute(origin, destination);
            } else if (RIDE_REQUEST.containsKey(DEST_ADD)
                    && !RIDE_REQUEST.get(DEST_ADD).equals("")
                    && CURRENT_STATUS.equalsIgnoreCase(PICKED_UP))
                extendRide();
        }
    }

    private void extendRide() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.destination_change))
                .setMessage(getString(R.string.destination_fare_changes))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    if (RIDE_REQUEST.containsKey(DEST_ADD)) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("latitude", RIDE_REQUEST.get(DEST_LAT));
                        map.put("longitude", RIDE_REQUEST.get(DEST_LONG));
                        map.put("address", RIDE_REQUEST.get(DEST_ADD));
                        map.put("request_id", DATUM.getId());
                        mainPresenter.updateDestination(map);
                        changeDestination.setText(String.valueOf(RIDE_REQUEST.get(DEST_ADD)));
                        LatLng origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
                        LatLng destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
                        drawRoute(origin, destination);
                    } else changeDestination.setText("");
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    @Override
    public void onDestinationSuccess(Object object) {
        System.out.println("RRR onDestinationSuccess");
    }

    @Override
    public void onSuccess(Message message) {

        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (message.getMessage().equals("Transaction Failed")) {
            Toast.makeText(this, "Card failed or insufficient balance! Please pay the driver in cash.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.you_have_successfully_paid, Toast.LENGTH_SHORT).show();
            this.changeFlow(RATING);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s = location.getLatitude() + "\n" + location.getLongitude()
                    + "\n\n" + getString(R.string.my_current_city)
                    + cityName;
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayCurrentAddress() {
        if (mGoogleMap == null) return;

        if (isLocationPermissionGranted) {
            if (mLastKnownLocation == null) {
                Toast.makeText(this, "waiting for your location ...", Toast.LENGTH_LONG).show();
                mLastKnownLocation = getLastKnownLocation();
                if (mLastKnownLocation != null) {
                    String address = getAddress(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                    sourceTxt.setText(address);
                    RIDE_REQUEST.put(SRC_ADD, address);
                    RIDE_REQUEST.put(SRC_LAT, mLastKnownLocation.getLatitude());
                    RIDE_REQUEST.put(SRC_LONG, mLastKnownLocation.getLongitude());
                }

            } else {
                if (TextUtils.isEmpty(sourceTxt.getText().toString()) || sourceTxt.getText().toString().equals(getText(R.string.pickup_location).toString())) {
                    String address = getAddress(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                    sourceTxt.setText(address);
                    RIDE_REQUEST.put(SRC_ADD, address);
                    RIDE_REQUEST.put(SRC_LAT, mLastKnownLocation.getLatitude());
                    RIDE_REQUEST.put(SRC_LONG, mLastKnownLocation.getLongitude());
                } else {
                    String address = getAddress(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                    sourceTxt.setText(address);
                    RIDE_REQUEST.put(SRC_ADD, address);
                    RIDE_REQUEST.put(SRC_LAT, mLastKnownLocation.getLatitude());
                    RIDE_REQUEST.put(SRC_LONG, mLastKnownLocation.getLongitude());

                }
            }

            hideLoading();
        } else getLocationPermission();
    }

    public void updatePaymentEntities() {
        if (checkStatusResponse != null) {
            isCash = checkStatusResponse.getCash() == 1;
            isCard = checkStatusResponse.getCard() == 1;
            if (checkStatusResponse.getOnline().equals("1")) {
                online = true;
            }
            isDebitMachine = checkStatusResponse.getDebitMachine() == 1;
            isVoucher = checkStatusResponse.getVoucher() == 1;

            MvpApplication.isPayumoney = checkStatusResponse.getPayumoney() == 1;
            MvpApplication.isPaypal = checkStatusResponse.getPaypal() == 1;
            MvpApplication.isBraintree = checkStatusResponse.getBraintree() == 1;
            MvpApplication.isPaypalAdaptive = checkStatusResponse.getPaypal_adaptive() == 1;
            MvpApplication.isPaytm = checkStatusResponse.getPaytm() == 1;

            SharedHelper.putKey(this, "currency", checkStatusResponse.getCurrency());
            if (isCash) RIDE_REQUEST.put(PAYMENT_MODE, Constants.PaymentMode.CASH);
            else if (isCard) RIDE_REQUEST.put(PAYMENT_MODE, Constants.PaymentMode.CARD);
            else if (isDebitMachine)
                RIDE_REQUEST.put(PAYMENT_MODE, Constants.PaymentMode.DEBIT_MACHINE);
            else if (isVoucher) RIDE_REQUEST.put(PAYMENT_MODE, Constants.PaymentMode.VOUCHER);
        }
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers)
            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) continue;
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
                    bestLocation = l;
            }
        return bestLocation;
    }

    @Override
    public void onSuccess(SettingsResponse response) {
        if (response.getReferral().getOnline().equals("1")) {
            online = true;
        }
        if (response.getReferral().getReferral().equalsIgnoreCase("1")) navMenuVisibility(true);
        else navMenuVisibility(false);
    }

    private void navMenuVisibility(boolean visibility) {
        //navigationView.getMenu().findItem(R.id.nav_invite_friend).setVisible(visibility);
    }

    @Override
    public void onSettingError(Throwable e) {
        navMenuVisibility(false);
    }

    @Override
    public void onSuccessSearch(List<SearchAddress> o) {
        searchAddressAdapter.update(o);
    }

    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapFragment.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapFragment.onStop();
    }

    private interface LatLngInterface {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterface {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                if (Math.abs(lngDelta) > 180) lngDelta -= Math.signum(lngDelta) * 360;
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }

}
