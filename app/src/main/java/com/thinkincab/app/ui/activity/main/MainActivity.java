package com.thinkincab.app.ui.activity.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.hold1.keyboardheightprovider.KeyboardHeightProvider;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.thinkincab.app.BuildConfig;
import com.thinkincab.app.MvpApplication;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseActivity;
import com.thinkincab.app.chat.ChatActivity;
import com.thinkincab.app.common.Constants;
import com.thinkincab.app.common.InfoWindowData;
import com.thinkincab.app.common.LocaleHelper;
import com.thinkincab.app.data.SharedHelper;
import com.thinkincab.app.data.network.model.AddressResponse;
import com.thinkincab.app.data.network.model.DataResponse;
import com.thinkincab.app.data.network.model.LatLngFireBaseDB;
import com.thinkincab.app.data.network.model.MenuDrawer;
import com.thinkincab.app.data.network.model.Message;
import com.thinkincab.app.data.network.model.Provider;
import com.thinkincab.app.data.network.model.SearchAddress;
import com.thinkincab.app.data.network.model.SearchRoute;
import com.thinkincab.app.data.network.model.SettingsResponse;
import com.thinkincab.app.data.network.model.User;
import com.thinkincab.app.data.network.model.UserAddress;
import com.thinkincab.app.ui.activity.help.HelpActivity;
import com.thinkincab.app.ui.activity.payment.PaymentActivity;
import com.thinkincab.app.ui.activity.setting.SettingsActivity;
import com.thinkincab.app.ui.activity.your_trips.YourTripActivity;
import com.thinkincab.app.ui.adapter.EmptyAddressAdapter;
import com.thinkincab.app.ui.adapter.SearchAddressAdapter;
import com.thinkincab.app.ui.adapter.UserAddressAdapter;
import com.thinkincab.app.ui.fragment.RateCardFragment;
import com.thinkincab.app.ui.fragment.book_ride.BookRideFragment;
import com.thinkincab.app.ui.fragment.invoice.InvoiceFragment;
import com.thinkincab.app.ui.fragment.map.IMapView;
import com.thinkincab.app.ui.fragment.map.MapFragment;
import com.thinkincab.app.ui.fragment.map_select.MapSelectFragment;
import com.thinkincab.app.ui.fragment.rate.RatingDialogFragment;
import com.thinkincab.app.ui.fragment.schedule.ScheduleFragment;
import com.thinkincab.app.ui.fragment.searching.SearchingFragment;
import com.thinkincab.app.ui.fragment.service.ServiceTypesFragment;
import com.thinkincab.app.ui.fragment.service_flow.ServiceFlowFragment;
import com.thinkincab.app.ui.fragment.sos.SosFragment;
import com.thinkincab.app.ui.utils.DisplayUtils;
import com.thinkincab.app.ui.utils.KeyboardUtils;
import com.thinkincab.app.ui.utils.ListOffset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import static com.thinkincab.app.common.Constants.Status.MAP;
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
        MainIView,
        IMapView,
        DrawerMenuListener {

    private static String CURRENT_STATUS = EMPTY;
    private final MainPresenter<MainActivity> mainPresenter = new MainPresenter<>();

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
    @BindView(R.id.on_map)
    FrameLayout onMap;
    @BindView(R.id.main_pin)
    View mainPin;

    private KeyboardHeightProvider keyboardHeightProvider;

    private boolean isEditable = true;
    private boolean isMapMoved = false;
    private boolean isDragging = false;
    private boolean isExpanded = false;

    private static final long REQUEST_PLACES_DELAY = 1000;
    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {

        }
    };

    private FusedLocationProviderClient mFusedLocation;
    private MapFragment mapFragment;
    private DatabaseReference mProviderLocation;

    private InfoWindowData destinationLeg;

    private boolean isDoubleBackPressed = false;
    private boolean isLocationPermissionGranted;
    private boolean canReRoute = true, canCarAnim = true;
    private int getProviderHitCheck;

    private LatLng start = null, end = null;
    private Location mLastKnownLocation;

    private DataResponse checkStatusResponse = new DataResponse();
    private UserAddress home = null, work = null;
    private UserAddressAdapter userAddressAdapter;
    private SearchAddressAdapter searchAddressAdapter;

    private DrawerFragment drawerFragment;
    private Runnable r;
    private Handler h;

    private MapSelectFragment mapSelectFragment;

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
        if (focused && view.getId() == R.id.destination) {
            onMap.setVisibility(View.VISIBLE);
        } else {
            onMap.setVisibility(View.GONE);
        }
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("map", false)) {
                topLayout.transitionToStart();
            } else {
                mainPresenter.checkStatus();
            }
        }
    };
    private LatLngBounds.Builder builder;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        MainPresenter.GEO_CACHE.clear();
        ButterKnife.bind(this);
        keyboardHeightProvider = new KeyboardHeightProvider(this);
        keyboardHeightProvider.addKeyboardListener(height -> {
            //addressesList.setPadding(0, 0, 0, height);
            ((ViewGroup.MarginLayoutParams) onMap.getLayoutParams()).bottomMargin = height + DisplayUtils.dpToPx(16);
            onMap.requestLayout();
        });
        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawer);

        registerReceiver(myReceiver, new IntentFilter(INTENT_FILTER));
        builder = new LatLngBounds.Builder();

        mainPresenter.attachView(this);

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();

        h = new Handler();
        r = () -> {
            getDeviceLocation();
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

                LatLngBounds bounds = builder.build();
                if (mapFragment != null) {
                    mapFragment.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
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
                    isExpanded = false;
                    KeyboardUtils.hideKeyboard(MainActivity.this, sourceTxt, destinationTxt);
                    pickLocationLayout.requestFocus();
                    topLayout.enableTransition(R.id.tr, false);
                    searchAddressAdapter.update(new ArrayList<>());
                    if (CURRENT_STATUS.equalsIgnoreCase(MAP)) {
                        changeFlow(CURRENT_STATUS);
                        motionLayout.setTransition(R.id.tr_to_map);
                        motionLayout.transitionToEnd();
                    } else if (RIDE_REQUEST.containsKey(SRC_ADD)
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
                    isExpanded = true;
                } else if (i == R.id.start_service) {
                    isExpanded = false;
                    motionLayout.setTransition(R.id.tr);
                    CURRENT_STATUS = EMPTY;
                    changeFlow(CURRENT_STATUS);
                    returnToInitial();
                } else if (i == R.id.start_map) {
                    isExpanded = false;
                    motionLayout.setTransition(R.id.tr);
                    if (RIDE_REQUEST.containsKey(DEST_ADD) && RIDE_REQUEST.get(DEST_ADD) != destinationTxt.getText().toString()) {
                        isEditable = false;
                        destinationTxt.setText((String) RIDE_REQUEST.get(DEST_ADD));
                        isEditable = true;
                    }
                    if (RIDE_REQUEST.containsKey(SRC_ADD)
                            && RIDE_REQUEST.containsKey(DEST_ADD)) {
                        CURRENT_STATUS = SERVICE;
                        changeFlow(CURRENT_STATUS);
                        LatLng origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
                        LatLng destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
                        drawRoute(origin, destination);
                        motionLayout.setTransition(R.id.tr_to_service);
                        motionLayout.transitionToEnd();
                    } else {
                        CURRENT_STATUS = EMPTY;
                        changeFlow(CURRENT_STATUS);
                    }
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
                if (mapFragment != null) {
                    isMapMoved = true;
                    mapFragment.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(new LatLng(
                                    item.getLat(),
                                    item.getLon()
                                    )
                            )
                            .padding(
                                    0.0,
                                    0.0,
                                    0.0,
                                    DisplayUtils.dpToPx(0) // current padding
                            )
                            .zoom(DEFAULT_ZOOM)
                            .build()));
                }
                setStartAddress(item);
                topLayout.transitionToStart();
            } else if (selectedEditText == R.id.destination) {
                isEditable = false;
                destinationTxt.setText(item.getShortAddress());
                RIDE_REQUEST.put(DEST_ADD, item.getShortAddress());
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
    protected void onPause() {
        super.onPause();
        if (keyboardHeightProvider != null) {
            keyboardHeightProvider.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (keyboardHeightProvider != null) {
            keyboardHeightProvider.onResume();
        }
        mainPresenter.getNavigationSettings();
        registerReceiver(myReceiver, new IntentFilter(INTENT_FILTER));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        isLocationPermissionGranted = false;
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
                getDeviceLocation();
            }
    }

    // functions

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
                //  mGoogleMap.clear();
                // providersMarker.clear();
                hideLoading();
                addAllProviders(SharedHelper.getProviders(this));
                changeFragment(null);
                btnHomeValue.setText(home != null ? R.string.home : R.string.add_home);
                btnWorkValue.setText(work != null ? R.string.work : R.string.add_work);
                mProviderLocation = null;
                if (mapFragment != null) {
                    mapFragment.deleteRoute();
                }
                mainPin.setVisibility(View.VISIBLE);
                break;
            case MAP:
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view));
                menuApp.setVisibility(View.GONE);
                menuBack.setVisibility(View.VISIBLE);
                mapSelectFragment = new MapSelectFragment();
                changeFragment(mapSelectFragment);
                if (mapFragment != null) {
                    mapFragment.deleteRoute();
                }
                mainPin.setVisibility(View.VISIBLE);
                break;
            case SERVICE:
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view));
                menuApp.setVisibility(View.GONE);
                menuBack.setVisibility(View.VISIBLE);
                updatePaymentEntities();
                changeFragment(new BookRideFragment());
                if (mapFragment != null) {
                    mapFragment.deleteRoute();
                }
                mainPin.setVisibility(View.GONE);
                break;
            case SEARCHING:
                pickLocationLayout.setVisibility(View.GONE);
                updatePaymentEntities();
                SearchingFragment searchingFragment = new SearchingFragment();
                searchingFragment.show(getSupportFragmentManager(), SEARCHING);
                break;
            case STARTED:
                // mGoogleMap.clear();
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

                ServiceFlowFragment flow = new ServiceFlowFragment();
                Bundle bn = new Bundle();
                bn.putString("show", "yes");
                flow.setArguments(bn);
                changeFragment(flow);
                break;
            case DROPPED:
            case COMPLETED:
                pickLocationLayout.setVisibility(View.GONE);
                changeFragment(new InvoiceFragment());
                break;
            case RATING:
                changeFragment(null);
                if (DATUM != null)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
                new RatingDialogFragment().show(getSupportFragmentManager(), RATING);
                RIDE_REQUEST.clear();
                //    mGoogleMap.clear();
                pickLocationLayout.setVisibility(View.GONE);
                sourceTxt.setText("");
                sourceTxt.setHint(getString(R.string.fetching_current_location));
                destinationTxt.setText("");
                break;
            default:
                break;
        }
    }

    private void setStartAddress(@Nullable SearchAddress address) {
        if (address != null) {
            isEditable = false;
            sourceTxt.setText(address.getShortAddress());
            RIDE_REQUEST.put(SRC_ADD, address.getShortAddress());
            RIDE_REQUEST.put(SRC_LAT, address.getLat());
            RIDE_REQUEST.put(SRC_LONG, address.getLon());
            isEditable = true;
        } else {
            isEditable = false;
            sourceTxt.setText("");
            eraseSrc.setVisibility(View.INVISIBLE);
            RIDE_REQUEST.remove(SRC_ADD);
            RIDE_REQUEST.remove(SRC_LAT);
            RIDE_REQUEST.remove(SRC_LONG);
            isEditable = true;
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

                                if (start != null) {
                                    SharedHelper.putCurrentLocation(MainActivity.this, start);
                                    end = start;
                                }
                                start = source;
                                if (end != null && canCarAnim) {
                                    if (start != null) {
                                        System.out.println("CAR ANIM ===>>");
                                        //   carAnim(srcMarker, end, start);
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

    public void drawRoute(LatLng source, LatLng destination) {
        mainPresenter.getRoute(source.getLatitude(), source.getLongitude(), destination.getLatitude(), destination.getLongitude());
    }

    public void changeFragment(Fragment fragment) {
        if (isFinishing()) return;
        if (fragment != null) {
            if (fragment instanceof BookRideFragment || fragment instanceof MapSelectFragment) {

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
            mapSelectFragment = null;
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

    void getDeviceLocation() {
        try {
            if (isLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocation.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastKnownLocation = task.getResult();
                        handleLocation(true);
                    } else {
                        handleLocation(false);
                    }
                });
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void handleLocation(boolean success) {
        if (mapFragment != null) {
            if (success) {
                if (mLastKnownLocation != null) {
                    if (!isMapMoved) {
                        mapFragment.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(new LatLng(
                                                mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()
                                        )
                                )
                                .padding(
                                        0.0,
                                        0.0,
                                        0.0,
                                        DisplayUtils.dpToPx(0) // current padding
                                )
                                .zoom(DEFAULT_ZOOM)
                                .build()));
                    }
                    SharedHelper.putKey(this, "latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                    SharedHelper.putKey(this, "longitude", String.valueOf(mLastKnownLocation.getLongitude()));
                }
            } else {
                mDefaultLocation = new LatLng(
                        Double.valueOf(SharedHelper.getKey(this, "latitude", "43.2973300")),
                        Double.valueOf(SharedHelper.getKey(this, "longitude", "68.2517500"))
                );
                if (mapFragment != null && !isMapMoved) {
                    mapFragment.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                            .target(mDefaultLocation)
                            .padding(
                                    0.0,
                                    0.0,
                                    0.0,
                                    DisplayUtils.dpToPx(0) // current padding
                            )
                            .zoom(DEFAULT_ZOOM)
                            .build()));
                    mapFragment.enableMyLocation(false);
                }
            }
        }
    }

    public boolean getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true;
            return true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
            return false;
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
                        LatLng origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
                        LatLng destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
                        drawRoute(origin, destination);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    private void displayCurrentAddress() {
        if (getLocationPermission()) {
            if (mLastKnownLocation == null) {
                mLastKnownLocation = getLastKnownLocation();
            }
            handleLocation(true);
            hideLoading();
        }
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

    private void addAllProviders(List<Provider> providers) {
       /* if (providers != null) for (Provider provider : providers)
            if (providersMarker.get(provider.getId()) != null) {
                Marker marker = providersMarker.get(provider.getId());
                LatLng startPos = marker.getPosition();
                LatLng endPos = new LatLng(provider.getLatitude(), provider.getLongitude());
                marker.setPosition(endPos);
                carAnim(marker, startPos, endPos);
            } else {
                int serviceIcon;
                if (provider.getProviderService().getServiceTypeId() == serviceMototaxiID) {
                    serviceIcon = R.drawable.car_icon_11;
                } else if (provider.getProviderService().getServiceTypeId() == serviceMotoboyID) {
                    serviceIcon = R.drawable.car_icon_11;
                } else {
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
            }*/
    }

    public void addSpecificProviders(List<Provider> providers, String key) {
       /* if (providers != null) {
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
           *//* Iterator<Map.Entry<Integer, Marker>> it = providersMarker.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Marker> pair = it.next();
                Marker marker = providersMarker.get(pair.getKey());
                marker.remove();
                it.remove();
            }*//*
            for (Provider provider : providers) {

                int serviceIcon;
                if (provider.getProviderService().getServiceTypeId() == serviceMototaxiID) {
                    serviceIcon = R.drawable.car_icon_11;
                } else if (provider.getProviderService().getServiceTypeId() == serviceMotoboyID) {
                    serviceIcon = R.drawable.car_icon_11;
                } else {
                    serviceIcon = R.drawable.car_icon_11;
                }

                new TheTask(provider.getId(), provider.getLatitude(), provider.getLongitude(), provider.getFirstName(), "", MvpApplication.marker).execute();
               *//* MarkerOptions markerOptions = new MarkerOptions()
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                        .rotation(0.0f)
                        .snippet("" + provider.getId())
                        .icon(BitmapDescriptorFactory.fromResource(serviceIcon));
                providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));*//*
            }
        }*/
    }

    // presenter

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
        } else if (CURRENT_STATUS.equals(SERVICE) || CURRENT_STATUS.equals(MAP))
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

    @Override
    public void onSuccess(SettingsResponse response) {
        if (response.getReferral().getOnline().equals("1")) {
            online = true;
        }
    }

    @Override
    public void onSettingError(Throwable e) {

    }

    @Override
    public void onSearchError(Throwable e) {

    }

    @Override
    public void onPointError(Throwable e) {
        Log.d("gdsdg", "onEr" + e.toString());
    }

    @Override
    public void onSuccessRoute(SearchRoute o) {
        if (CURRENT_STATUS.equalsIgnoreCase(SERVICE) && mapFragment != null) {
            mapFragment.showRoute(o);
        }
    }

    @Override
    public void onSuccessSearch(List<SearchAddress> o) {
        searchAddressAdapter.update(o);
    }

    @Override
    public void onSuccessPoint(SearchAddress o) {
        if (CURRENT_STATUS.equals(EMPTY) && !isExpanded && o != null && !isDragging) {
            setStartAddress(o);
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

    @Override
    public void onError(Throwable e) {

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

    // clicks

    @OnClick({R.id.on_map, R.id.sos, R.id.erase_src, R.id.erase_dest, R.id.btn_home, R.id.btn_work, R.id.menu_app, R.id.gps, R.id.source, R.id.destination, R.id.menu_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.on_map:
                CURRENT_STATUS = MAP;
                topLayout.transitionToStart();
                break;
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
                setStartAddress(null);
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
                    if (mapFragment != null) {
                        mapFragment.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(currentLatLng)
                                .padding(
                                        0.0,
                                        0.0,
                                        0.0,
                                        DisplayUtils.dpToPx(0) // current padding
                                )
                                .zoom(DEFAULT_ZOOM)
                                .build()));
                    }
                    isMapMoved = false;
                }
                break;
            case R.id.source:
                CURRENT_STATUS = EMPTY;
                if (topLayout.getProgress() == 0) {
                    topLayout.enableTransition(R.id.tr, true);
                    topLayout.transitionToEnd();
                }
                break;
            case R.id.destination:
                CURRENT_STATUS = EMPTY;
                if (topLayout.getProgress() == 0) {
                    topLayout.enableTransition(R.id.tr, true);
                    topLayout.transitionToEnd();
                }
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

    // drawer

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

    // map

    @Override
    public void onMapMoved(boolean moved) {
        isMapMoved = moved;
    }

    @Override
    public void onActionUp(LatLng point) {
        if (CURRENT_STATUS.equalsIgnoreCase(EMPTY)) {
            isDragging = false;
            mainPresenter.startSearch(point.getLatitude(), point.getLongitude());
        } else if (CURRENT_STATUS.equalsIgnoreCase(MAP)) {
            if (mapSelectFragment != null) {
                mapSelectFragment.onActionUp(point);
            }
        }
    }

    @Override
    public void onActionDown() {
        if (CURRENT_STATUS.equalsIgnoreCase(EMPTY)) {
            isDragging = true;
            setStartAddress(null);
        } else if (CURRENT_STATUS.equalsIgnoreCase(MAP)) {
            if (mapSelectFragment != null) {
                mapSelectFragment.onActionDown();
            }
        }
    }

    @Override
    public void onMapReady() {
        displayCurrentAddress();
    }

    @Override
    public int getMapPadding() {
        return DisplayUtils.dpToPx(0); // current padding;
    }
}
