package kz.yassy.taxi.ui.activity.main;

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
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hold1.keyboardheightprovider.KeyboardHeightProvider;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import kz.yassy.taxi.BuildConfig;
import kz.yassy.taxi.MvpApplication;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.chat.ChatActivity;
import kz.yassy.taxi.common.ConnectionLiveData;
import kz.yassy.taxi.common.Constants;
import kz.yassy.taxi.common.LocaleHelper;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.model.AddressResponse;
import kz.yassy.taxi.data.network.model.CheckVersion;
import kz.yassy.taxi.data.network.model.DataResponse;
import kz.yassy.taxi.data.network.model.MenuDrawer;
import kz.yassy.taxi.data.network.model.Message;
import kz.yassy.taxi.data.network.model.Provider;
import kz.yassy.taxi.data.network.model.SearchAddress;
import kz.yassy.taxi.data.network.model.SearchRoute;
import kz.yassy.taxi.data.network.model.SettingsResponse;
import kz.yassy.taxi.data.network.model.User;
import kz.yassy.taxi.data.network.model.UserAddress;
import kz.yassy.taxi.ui.activity.favorites.FavoritesActivity;
import kz.yassy.taxi.ui.activity.help.HelpActivity;
import kz.yassy.taxi.ui.activity.help.InfoActivity;
import kz.yassy.taxi.ui.activity.notification_manager.NotificationManagerActivity;
import kz.yassy.taxi.ui.activity.payment.PaymentActivity;
import kz.yassy.taxi.ui.activity.setting.SettingsActivity;
import kz.yassy.taxi.ui.activity.your_trips.YourTripActivity;
import kz.yassy.taxi.ui.adapter.EmptyAddressAdapter;
import kz.yassy.taxi.ui.adapter.SearchAddressAdapter;
import kz.yassy.taxi.ui.adapter.UserAddressAdapter;
import kz.yassy.taxi.ui.fragment.RateCardFragment;
import kz.yassy.taxi.ui.fragment.book_ride.BookRideFragment;
import kz.yassy.taxi.ui.fragment.error.ErrorFragment;
import kz.yassy.taxi.ui.fragment.invoice.InvoiceFragment;
import kz.yassy.taxi.ui.fragment.map.IMapView;
import kz.yassy.taxi.ui.fragment.map.MapFragment;
import kz.yassy.taxi.ui.fragment.map_select.MapSelectFragment;
import kz.yassy.taxi.ui.fragment.rate.RatingDialogFragment;
import kz.yassy.taxi.ui.fragment.rate.ShowUpdateDialogFragment;
import kz.yassy.taxi.ui.fragment.schedule.ScheduleFragment;
import kz.yassy.taxi.ui.fragment.searching.SearchingFragment;
import kz.yassy.taxi.ui.fragment.service_flow.ServiceFlowFragment;
import kz.yassy.taxi.ui.fragment.sos.SosFragment;
import kz.yassy.taxi.ui.utils.DisplayUtils;
import kz.yassy.taxi.ui.utils.KeyboardUtils;
import kz.yassy.taxi.ui.utils.ListOffset;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static kz.yassy.taxi.MvpApplication.DATUM;
import static kz.yassy.taxi.MvpApplication.RIDE_REQUEST;
import static kz.yassy.taxi.MvpApplication.canGoToChatScreen;
import static kz.yassy.taxi.MvpApplication.isCard;
import static kz.yassy.taxi.MvpApplication.isCash;
import static kz.yassy.taxi.MvpApplication.isChatScreenOpen;
import static kz.yassy.taxi.MvpApplication.isDebitMachine;
import static kz.yassy.taxi.MvpApplication.isVoucher;
import static kz.yassy.taxi.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_ADD;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LONG;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.PAYMENT_MODE;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_ADD;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LONG;
import static kz.yassy.taxi.common.Constants.Status.ARRIVED;
import static kz.yassy.taxi.common.Constants.Status.COMPLETED;
import static kz.yassy.taxi.common.Constants.Status.EMPTY;
import static kz.yassy.taxi.common.Constants.Status.MAP;
import static kz.yassy.taxi.common.Constants.Status.PICKED_UP;
import static kz.yassy.taxi.common.Constants.Status.RATING;
import static kz.yassy.taxi.common.Constants.Status.SEARCHING;
import static kz.yassy.taxi.common.Constants.Status.SERVICE;
import static kz.yassy.taxi.common.Constants.Status.SOS;
import static kz.yassy.taxi.common.Constants.Status.STARTED;
import static kz.yassy.taxi.data.SharedHelper.key.PROFILE_IMG;
import static kz.yassy.taxi.data.SharedHelper.key.SOS_NUMBER;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity implements
        MainIView,
        IMapView,
        DrawerMenuListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static String CURRENT_STATUS = EMPTY;
    private final MainPresenter<MainActivity> mainPresenter = new MainPresenter<>();

    private final int[] transitions = new int[]{R.id.tr_to_trip, R.id.tr_to_map, R.id.tr_to_service, R.id.tr};

    @BindView(R.id.container)
    FrameLayout container;
    private final TextWatcher filterTextWatcher = new TextWatcher() {


        public void afterTextChanged(Editable editable) {
            eraseSrc.setVisibility(sourceTxt.hasFocus() && editable.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            eraseDest.setVisibility(destinationTxt.hasFocus() && editable.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            Log.e("searchText", editable.toString());
            requestPlacesByDelay(editable);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };
    @BindView(R.id.menu_back)
    ImageView menuBack;
    @BindView(R.id.menu_app)
    ImageView menuApp;
    @BindView(R.id.gps)
    ImageView gps;
    @BindView(R.id.containerMine)
    FrameLayout containerMine;
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
    @BindView(R.id.alert_wrapper)
    View alertWrapper;
    @BindView(R.id.sos)
    TextView sos;
    @BindView(R.id.errorContainer)
    LinearLayout errorContainer;
    @BindView(R.id.errorMain)
    TextView errorMain;

    private KeyboardHeightProvider keyboardHeightProvider;

    private boolean isEditable = true;
    private boolean isMapMoved = false;
    private boolean isDragging = false;
    private boolean isExpanded = false;
    private boolean connected = false;

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

    private boolean isDoubleBackPressed = false;
    private boolean isLocationPermissionGranted;
    private int getProviderHitCheck;

    private Location mLastKnownLocation;

    private DataResponse checkStatusResponse = new DataResponse();
    private UserAddress home = null, work = null;
    private UserAddressAdapter userAddressAdapter;
    private SearchAddressAdapter searchAddressAdapter;
    @BindView(R.id.error)
    TextView error;

    private DrawerFragment drawerFragment;
    private Runnable r;
    private Handler h;

    private LatLng lastPoint;

    private MapSelectFragment mapSelectFragment;
    private Fragment currentTripFragment;

    private int selectedEditText;
    private EmptyAddressAdapter emptyAddressAdapter;
    private boolean showRatingDialogFragment = false;
    private boolean AllowToDismissRating = true;
    private boolean firstIn = true;
    private RatingDialogFragment ratingDialogFragment;
    private double theLastLatitude, theLastLongitude;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

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
                Log.e("af;ldjfalk;fdja;lf", "afkjaf;lkajsdflajf");
            } else {
                mainPresenter.checkStatus();
            }
            if (SharedHelper.getKey(getApplicationContext(), "cancelRideActivity", false)) {
                MvpApplication.RIDE_REQUEST.remove(DEST_ADD);
                MvpApplication.RIDE_REQUEST.remove(DEST_LAT);
                MvpApplication.RIDE_REQUEST.remove(DEST_LONG);
                changeFlow("EMPTY", true);
                SharedHelper.putKey(getApplicationContext(), "cancelRideActivity", false);
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        SharedHelper.putKey(getApplicationContext(), "cancelRideActivity", false);
        MainPresenter.GEO_CACHE.clear();
        ButterKnife.bind(this);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        keyboardHeightProvider = new KeyboardHeightProvider(this);
        keyboardHeightProvider.addKeyboardListener(height -> {
            //addressesList.setPadding(0, 0, 0, height);
            ((ViewGroup.MarginLayoutParams) onMap.getLayoutParams()).bottomMargin = height + DisplayUtils.dpToPx(60);
            onMap.requestLayout();
        });
        SharedHelper.putKey(getApplicationContext(), "showLocationError", false);
        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawer);
        registerReceiver(myReceiver, new IntentFilter(INTENT_FILTER));
        mainPresenter.attachView(this);
        mainPresenter.checkUpdate();
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = new MapFragment();
        LinearLayout bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        String msg = "fj;lafja";
        pickLocationLayout.setOnDragListener((view, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d(msg, "Action is DragEvent.ACTION_DRAG_STARTED");

                    // Do nothing
                    break;

                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENTERED");
                    int x_cord = (int) event.getX();
                    int y_cord = (int) event.getY();
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d(msg, "Action is DragEvent.ACTION_DRAG_EXITED");
                    x_cord = (int) event.getX();
                    y_cord = (int) event.getY();
                    break;

                case DragEvent.ACTION_DRAG_LOCATION:
                    Log.d(msg, "Action is DragEvent.ACTION_DRAG_LOCATION");
                    x_cord = (int) event.getX();
                    y_cord = (int) event.getY();
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENDED");

                    // Do nothing
                    break;

                case DragEvent.ACTION_DROP:
                    Log.d(msg, "ACTION_DROP event");

                    // Do nothing
                    break;
                default:
                    break;
            }
            return true;
        });
        h = new Handler();
        getProviderHitCheck = 0;
        r = () -> {
            getDeviceLocation();
            if (mLastKnownLocation != null) {
                mainPresenter.checkStatus();
            }
            mainPresenter.checkStatus();
            if (CURRENT_STATUS.equals(SERVICE) || CURRENT_STATUS.equals(EMPTY)) {
                if (mLastKnownLocation != null) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("latitude", mLastKnownLocation.getLatitude());
                    map.put("longitude", mLastKnownLocation.getLongitude());
                    mainPresenter.getProviders(map);
                }
            } else if (CURRENT_STATUS.equals(STARTED) || CURRENT_STATUS.equals(ARRIVED) || CURRENT_STATUS.equals(PICKED_UP)) {
                try {
                    if ((CURRENT_STATUS.equals(STARTED) || CURRENT_STATUS.equals(ARRIVED)) && DATUM != null) {
                        mapFragment.clearAllMarker();
                        addMarker(new LatLng(DATUM.getProviderLatitude(), DATUM.getProviderLongitude()));
                        drawRoute(new LatLng(DATUM.getProviderLatitude(), DATUM.getProviderLongitude()), new LatLng(DATUM.getSLatitude(), DATUM.getSLongitude()), getProviderHitCheck < 2);
                    } else if (CURRENT_STATUS.equals(PICKED_UP) && DATUM != null) {
                        mapFragment.clearAllMarker();
                        addMarker(new LatLng(DATUM.getProviderLatitude(), DATUM.getProviderLongitude()));
                        drawRoute(new LatLng(new LatLng(DATUM.getProviderLatitude(), DATUM.getProviderLongitude())), new LatLng(new LatLng(DATUM.getDLatitude(), DATUM.getDLongitude())), getProviderHitCheck < 2);
                    }
                    Log.e("LatLng", DATUM.getProviderLatitude() + " " + DATUM.getProviderLongitude());
//                    Toast.makeText(getApplicationContext(), DATUM.getProviderLatitude() + " " + DATUM.getProviderLongitude(), Toast.LENGTH_SHORT).show();
//                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                        builder.include(new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG)));
//                        LatLngBounds bounds = builder.build();
//                        if (mapFragment != null) {
//                            mapFragment.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
//                                    DisplayUtils.dpToPx(42),
//                                    DisplayUtils.dpToPx(100),
//                                    DisplayUtils.dpToPx(42),
//                                    DisplayUtils.dpToPx(90) + getMapPadding()
//                                    )
//                            );
//                        }
                } catch (Exception e) {

                }
            }
            Log.e("dartMine", "dart");
            getProviderHitCheck++;
            h.postDelayed(r, 5000);
        };
        if (SharedHelper.getKey(getApplicationContext(), "doNotShowBetaVersion", false)) {
            ((RelativeLayout) findViewById(R.id.betaVersion)).setVisibility(View.GONE);
        }
        h.postDelayed(r, 0);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    // Do something for lollipop and above versions

                    Window window = getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // finally change the color to any color with transparency

                    window.setStatusBarColor(getResources().getColor(R.color.app_border_grey));
                }
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
//                menuBack.setVisibility(View.VISIBLE);
                //need improvement
                menuApp.setVisibility(View.VISIBLE);
                Log.e("This is called", "outside");
                if (i == R.id.start) {
                    isExpanded = false;
                    Log.e("This is called", "start");
                    KeyboardUtils.hideKeyboard(MainActivity.this, sourceTxt, destinationTxt);
                    pickLocationLayout.requestFocus();
                    topLayout.enableTransition(R.id.tr, false);
                    searchAddressAdapter.update(new ArrayList<>());
                    emptyAddressAdapter.update(1);
                    if (CURRENT_STATUS.equalsIgnoreCase(MAP)) {
                        changeFlow(CURRENT_STATUS, false);
                        motionLayout.setTransition(R.id.tr_to_map);
                        motionLayout.transitionToEnd();
                    } else if (RIDE_REQUEST.containsKey(SRC_ADD)
                            && RIDE_REQUEST.containsKey(DEST_ADD)
                            && CURRENT_STATUS.equalsIgnoreCase(EMPTY)) {
                        CURRENT_STATUS = SERVICE;
                        changeFlow(CURRENT_STATUS, false);
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
                    menuApp.setVisibility(View.GONE);
                    menuBack.setVisibility(View.GONE);
                    Log.e("This is called", "end");
                    //need improvement
                } else if (i == R.id.start_trip) {
                    isExpanded = false;
                    motionLayout.setTransition(R.id.tr);
                    Log.e("This is called", "starttrip");
                } else if (i == R.id.start_service) {
                    isExpanded = false;
                    motionLayout.setTransition(R.id.tr);
                    CURRENT_STATUS = EMPTY;
                    changeFlow(CURRENT_STATUS, false);
                    returnToInitial();
                    Log.e("This is called", "startService");
                } else if (i == R.id.start_map) {
                    isExpanded = false;
                    Log.e("This is called", "startmap");
                    motionLayout.setTransition(R.id.tr);
                    if (RIDE_REQUEST.containsKey(DEST_ADD) && RIDE_REQUEST.get(DEST_ADD) != destinationTxt.getText().toString()) {
                        isEditable = false;
                        destinationTxt.setText((String) RIDE_REQUEST.get(DEST_ADD));
                        isEditable = true;
                    }
                    if (RIDE_REQUEST.containsKey(SRC_ADD)
                            && RIDE_REQUEST.containsKey(DEST_ADD)) {
                        CURRENT_STATUS = SERVICE;
                        changeFlow(CURRENT_STATUS, false);
                        LatLng origin = new LatLng((Double) RIDE_REQUEST.get(SRC_LAT), (Double) RIDE_REQUEST.get(SRC_LONG));
                        LatLng destination = new LatLng((Double) RIDE_REQUEST.get(DEST_LAT), (Double) RIDE_REQUEST.get(DEST_LONG));
                        drawRoute(origin, destination);
                        motionLayout.setTransition(R.id.tr_to_service);
                        motionLayout.transitionToEnd();
                    } else {
                        CURRENT_STATUS = EMPTY;
                        changeFlow(CURRENT_STATUS, false);
                    }
                } else if (i == R.id.end_map) {
                    Log.e("This is called", "endmap");
                    if (mapSelectFragment != null && lastPoint != null) {
                        mapSelectFragment.onActionUp(lastPoint);
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
            Log.e("typeMine", item.getType());
            if (item.getType().equalsIgnoreCase("home")) {
                updateSavedAddress(home);
            } else {
                updateSavedAddress(work);
            }
        });
        emptyAddressAdapter = new EmptyAddressAdapter(1);
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
                                    getActualPadding()
                            )
                            .zoom(DEFAULT_ZOOM)
                            .build()));
                }
                setStartAddress(item);
                topLayout.transitionToStart();
            } else if (selectedEditText == R.id.destination) {
                isEditable = false;
                destinationTxt.setText(item.getValue());
                RIDE_REQUEST.put(DEST_ADD, item.getValue());
                RIDE_REQUEST.put(DEST_LONG, item.getLon());
                RIDE_REQUEST.put(DEST_LAT, item.getLat());
                isEditable = true;
                topLayout.transitionToStart();
            }
        });
        ConcatAdapter adapter = new ConcatAdapter(userAddressAdapter, searchAddressAdapter, emptyAddressAdapter);
        addressesList.addItemDecoration(new ListOffset(DisplayUtils.dpToPx(24)));
        addressesList.setAdapter(adapter);
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(this);
        connectionLiveData.observe(this, connected -> {
            this.connected = connected != null ? connected : false;
            if (this.connected) {
                alertWrapper.setVisibility(View.GONE);
            } else {
                alertWrapper.setVisibility(View.VISIBLE);
            }
        });
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
        if (showRatingDialogFragment) {
            ratingDialogFragment.show(getSupportFragmentManager(), null);
            showRatingDialogFragment = false;
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
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
                changeFlow(CURRENT_STATUS, false);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isLocationPermissionGranted = false;
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionGranted = true;
                getDeviceLocation();
            } else {
                showLocationError();
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
//                    if (isEditable && !s.toString().trim().isEmpty()) {
//                        mainPresenter.startSearch(s.toString());
//                    }
                    Log.e("startSearch", s.toString());
                    mainPresenter.startSearch(s.toString());
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

    public void changeFlow(String status, boolean rollbackTransition) {
        Log.d("TRIP_INFO", "status - " + status + ", " + rollbackTransition);
        if (rollbackTransition) {
            topLayout.transitionToStart();
        }
        if (AllowToDismissRating)
            dismissDialog(RATING);
        switch (status) {
            case EMPTY:
                CURRENT_STATUS = EMPTY;
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.nav_view));
                menuApp.setVisibility(View.VISIBLE);
                menuBack.setVisibility(View.GONE);
                Log.e("Empty", "activated");
                pickLocationLayout.setVisibility(View.VISIBLE);
                hideLoading();
                changeFragment(null);
                btnHomeValue.setText(home != null ? R.string.home : R.string.add_home);
                btnWorkValue.setText(work != null ? R.string.work : R.string.add_work);
                mProviderLocation = null;
                if (mapFragment != null) {
                    mapFragment.deleteRoute();
                    mapFragment.deleteRouteMine();
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
                Log.e("Map", "activated");
                mainPin.setVisibility(View.VISIBLE);
                break;
            case SERVICE:
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view));
                menuApp.setVisibility(View.GONE);
                menuBack.setVisibility(View.VISIBLE);
                topLayout.setTransition(R.id.tr_to_trip);
                topLayout.transitionToEnd();
                pickLocationLayout.setVisibility(View.GONE);
                //updatePaymentEntities();
                changeFragment(new BookRideFragment());

                if (mapFragment != null) {
                    mapFragment.deleteRoute();
                }
                Log.e("Service", "activated");
                mainPin.setVisibility(View.GONE);
                break;
            case SEARCHING:
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view));
                menuApp.setVisibility(View.GONE);
                menuBack.setVisibility(View.GONE);
                pickLocationLayout.setVisibility(View.GONE);
                updatePaymentEntities();
                Log.e("searchingFragment", "activated");
                SearchingFragment searchingFragment = new SearchingFragment();
                changeFragment(searchingFragment);
                break;
            case STARTED:
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view));
                menuApp.setVisibility(View.GONE);
                menuBack.setVisibility(View.GONE);
                pickLocationLayout.setVisibility(View.GONE);

                topLayout.setTransition(R.id.tr_to_trip);
                topLayout.transitionToEnd();
                if (mapFragment != null) {
                    mapFragment.deleteRoute();
                }
                if (DATUM != null)
                    FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(DATUM.getId()));
                changeFragment(null);
                changeFragmentMine(new ServiceFlowFragment());
                mainPin.setVisibility(View.GONE);
                Log.e("Started", "activated");
                break;
            case ARRIVED:
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view));
                menuApp.setVisibility(View.VISIBLE);
                menuBack.setVisibility(View.GONE);
                topLayout.setTransition(R.id.tr_to_trip);
                topLayout.transitionToEnd();
                pickLocationLayout.setVisibility(View.GONE);
                Log.e(">>>>", "arrived");
                changeFragment(null);
                if (mapFragment != null) {
                    mapFragment.deleteRoute();
                }
                Log.e("Arrived", "activated");
                changeFragmentMine(new ServiceFlowFragment());
                mainPin.setVisibility(View.GONE);
                break;
            case PICKED_UP:
                menuApp.setVisibility(View.VISIBLE);
                menuBack.setVisibility(View.GONE);
                pickLocationLayout.setVisibility(View.GONE);
                ServiceFlowFragment flow = new ServiceFlowFragment();
                Bundle bn = new Bundle();
                changeFragment(null);
                topLayout.setTransition(R.id.tr_to_trip);
                topLayout.transitionToEnd();
                bn.putString("show", "yes");
                Log.e("Picked_Up", "activated");
                flow.setArguments(bn);
                changeFragmentMine(flow);
                mainPin.setVisibility(View.GONE);
                break;
            case COMPLETED:
//                pickLocationLayout.setVisibility(View.GONE);
//                changeFragment(new InvoiceFragment());
                changeFragmentMine(null);
                if (DATUM != null)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
                showRatingDialogFragment = false;
                try {
                    if (ratingDialogFragment != null
                            && ratingDialogFragment.getDialog() != null
                            && ratingDialogFragment.getDialog().isShowing()
                            && !ratingDialogFragment.isRemoving()) {

                    } else {
                        ratingDialogFragment = new RatingDialogFragment();
                        ratingDialogFragment.show(getSupportFragmentManager(), RATING);
                    }
                } catch (Exception exception) {
                    showRatingDialogFragment = true;
                }
                Log.e("showRatingDialog", showRatingDialogFragment + " ");
                if (!showRatingDialogFragment) {
                    AllowToDismissRating = false;
                }
                RIDE_REQUEST.clear();
                //    mGoogleMap.clear();
                pickLocationLayout.setVisibility(View.VISIBLE);
                sourceTxt.setText("");
                sourceTxt.setHint(getString(R.string.fetching_current_location));
                destinationTxt.setText("");
                topLayout.transitionToStart();
                onMap.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void setStartAddress(@Nullable SearchAddress address) {
        if (address != null) {
            isEditable = false;
            sourceTxt.setText(address.getValue());
            RIDE_REQUEST.put(SRC_ADD, address.getValue());
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
            changeFlow(CURRENT_STATUS, true);
        }
    }

    public void drawRoute(LatLng source, LatLng destination) {
        Log.e("draweRoute", "here");
//        mainPresenter.getRoute(source.getLatitude(), source.getLongitude(), destination.getLatitude(), destination.getLongitude());
        mapFragment.getRoute(Point.fromLngLat(source.getLongitude(), source.getLatitude()), Point.fromLngLat(destination.getLongitude(), destination.getLatitude()));
    }

    public void drawRoute(LatLng source, LatLng destination, boolean animation) {
        Log.e("draweRoute", "here");
//        mainPresenter.getRoute(source.getLatitude(), source.getLongitude(), destination.getLatitude(), destination.getLongitude());
        mapFragment.getRoute(Point.fromLngLat(source.getLongitude(), source.getLatitude()), Point.fromLngLat(destination.getLongitude(), destination.getLatitude()), animation);
    }

    public void deleteAndDrawRoute(LatLng source, LatLng destination) {
        Log.e("draweRoute", ">>>>>>>>>>>>>>>>>>");
        mapFragment.deleteRoute();
        mainPresenter.getRoute(source.getLatitude(), source.getLongitude(), destination.getLatitude(), destination.getLongitude());
    }

    public void changeFragmentMine(Fragment fragment) {
        if (isFinishing()) return;
        if (fragment != null) {
            if (currentTripFragment != null && currentTripFragment instanceof ServiceFlowFragment && fragment instanceof ServiceFlowFragment) {
                return;
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (fragment instanceof RateCardFragment) {
                fragmentTransaction.addToBackStack(fragment.getTag());
            } else if (fragment instanceof ScheduleFragment) {
                fragmentTransaction.addToBackStack(fragment.getTag());
            } else if (fragment instanceof BookRideFragment) {
                container.setBackgroundResource(R.drawable.form_bg);
                fragmentTransaction.addToBackStack(fragment.getTag());
            } else if (fragment instanceof MapSelectFragment) {
                container.setBackgroundResource(R.drawable.form_bg);
            } else {
                container.setBackgroundResource(0);
            }
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            try {
                fragmentTransaction.replace(R.id.containerMine, fragment, fragment.getTag());
                fragmentTransaction.commitAllowingStateLoss();
                currentTripFragment = fragment;
            } catch (Exception e) {
                currentTripFragment = null;
                e.printStackTrace();
            }
        } else {
            currentTripFragment = null;
            mapSelectFragment = null;
            for (Fragment fragmentd : getSupportFragmentManager().getFragments()) {
                if (fragmentd instanceof ServiceFlowFragment) {
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
                }
                if (fragmentd instanceof InvoiceFragment)
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
            }
            containerMine.removeAllViews();
            try {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void changeFragment(Fragment fragment) {
        if (isFinishing()) return;
        if (fragment != null) {
            if (currentTripFragment != null && currentTripFragment instanceof ServiceFlowFragment && fragment instanceof ServiceFlowFragment) {
                return;
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (fragment instanceof RateCardFragment) {
                fragmentTransaction.addToBackStack(fragment.getTag());
            } else if (fragment instanceof ScheduleFragment) {
                fragmentTransaction.addToBackStack(fragment.getTag());
            } else if (fragment instanceof BookRideFragment) {
                container.setBackgroundResource(R.drawable.form_bg);
                fragmentTransaction.addToBackStack(fragment.getTag());
            } else if (fragment instanceof MapSelectFragment) {
                container.setBackgroundResource(R.drawable.form_bg);
            } else {
                container.setBackgroundResource(0);
            }

            try {
                fragmentTransaction.replace(R.id.container, fragment, fragment.getTag());
                fragmentTransaction.commitAllowingStateLoss();
                currentTripFragment = fragment;
            } catch (Exception e) {
                currentTripFragment = null;
                e.printStackTrace();
            }
        } else {
            currentTripFragment = null;
            mapSelectFragment = null;
            for (Fragment fragmentd : getSupportFragmentManager().getFragments()) {
                if (fragmentd instanceof ServiceFlowFragment) {
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
                }
                if (fragmentd instanceof InvoiceFragment)
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
            }
            container.removeAllViews();
            try {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showTime(String time) {
        if (CURRENT_STATUS.equalsIgnoreCase(SERVICE) && mapFragment != null) {
            mapFragment.showTime(time);
        }
    }

    private void dismissDialog(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof RatingDialogFragment) {
            RatingDialogFragment df = (RatingDialogFragment) fragment;
            df.dismissAllowingStateLoss();
        }
    }

    private void showLocationError() {
        if (!SharedHelper.getKey(getApplicationContext(), "showLocationError", false)) {
            ErrorFragment errorFragment = ErrorFragment.newInstance(ErrorFragment.LOCATION);
            try {
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                errorFragment.show(getSupportFragmentManager(), "ERROR");
                SharedHelper.putKey(getApplicationContext(), "showLocationError", true);
            } catch (Exception ignored) {
            }
        }
    }

    private void getDeviceLocation() {
        try {
            if (isLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocation.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mLastKnownLocation = task.getResult();
                        SharedHelper.putKey(getApplicationContext(), "lastKnownLocationLat", mLastKnownLocation.getLatitude());
                        SharedHelper.putKey(getApplicationContext(), "lastKnownLocationLong", mLastKnownLocation.getLongitude());
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
                                        getActualPadding()
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
                                    getActualPadding()
                            )
                            .zoom(DEFAULT_ZOOM)
                            .build()));
                    mapFragment.enableMyLocation(false);
                }
                showLocationError();
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
            if (checkStatusResponse.getOnline() == 0) {
                online = true;
            }
            isDebitMachine = checkStatusResponse.getDebitMachine() == 1;
            isVoucher = checkStatusResponse.getVoucher() == 1;
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

    private int getActualPadding() {
        switch (CURRENT_STATUS) {
            case SERVICE:
                return DisplayUtils.dpToPx(420); // TODO
            default:
                return 0;
        }
    }

    // presenter


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onSuccess(DataResponse dataResponse) {
        this.checkStatusResponse = dataResponse;
        updatePaymentEntities();
        SharedHelper.putKey(this, SOS_NUMBER, checkStatusResponse.getSos());
        if (dataResponse.getData() != null) {
            if (!CURRENT_STATUS.equals(dataResponse.getData().getStatus())) {
                DATUM = dataResponse.getData();
                final boolean rollback = DATUM.getStatus().equals(EMPTY) && !CURRENT_STATUS.equals(EMPTY);
                CURRENT_STATUS = DATUM.getStatus();
                Log.e("TRIP_INFO", "status by data - " + CURRENT_STATUS);
                Log.e("onSuccess", "Srazu");
                changeFlow(CURRENT_STATUS, rollback);
            }
            if (firstIn) {
                DATUM = dataResponse.getData();
                final boolean rollback = DATUM.getStatus().equals(EMPTY) && !CURRENT_STATUS.equals(EMPTY);
                CURRENT_STATUS = DATUM.getStatus();
                Log.e("TRIP_INFO", "status by data - " + CURRENT_STATUS);
                Log.e("onSuccess", "Srazu");

                changeFlow(CURRENT_STATUS, rollback);
                firstIn = false;
            }
        } else if (CURRENT_STATUS.equals(SERVICE) || CURRENT_STATUS.equals(MAP)) {
            System.out.println("RRR CURRENT_STATUS = " + CURRENT_STATUS);
        } else {
            Log.e("TRIP_INFO", "status clear - " + CURRENT_STATUS);
            CURRENT_STATUS = EMPTY;
            changeFlow(CURRENT_STATUS, false);
        }
        Log.e("onSuccess", ">>>>>>>>>>>>>>>>>");
        Log.e("euieuiuieiu", "euiiiuiiui" + dataResponse.getData().getStatus());
        if (CURRENT_STATUS.equals(STARTED)
                || CURRENT_STATUS.equals(ARRIVED)
                || CURRENT_STATUS.equals(PICKED_UP)) {

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
    public void onSuccessCheckStatus(DataResponse dataResponse) {
        this.checkStatusResponse = dataResponse;
        Log.e("StatusChange", dataResponse.getData().getStatus());
        updatePaymentEntities();
        SharedHelper.putKey(this, SOS_NUMBER, checkStatusResponse.getSos());
        if (dataResponse.getData() != null) {
            if (firstIn) {
                DATUM = dataResponse.getData();
                final boolean rollback = DATUM.getStatus().equals(EMPTY) && !CURRENT_STATUS.equals(EMPTY);
                CURRENT_STATUS = DATUM.getStatus();
                Log.e("euieuiuieiu", "firstIn" + dataResponse.getData().getStatus());
                changeFlow(CURRENT_STATUS, rollback);
                firstIn = false;
//                h.removeCallbacks(r);
//                h.postDelayed(r, 0);
            }
            if (!CURRENT_STATUS.equals(dataResponse.getData().getStatus())) {
                DATUM = dataResponse.getData();
                final boolean rollback = DATUM.getStatus().equals(EMPTY) && !CURRENT_STATUS.equals(EMPTY);
                CURRENT_STATUS = DATUM.getStatus();
                Log.e("euieuiuieiu", "euiiiuiiui" + dataResponse.getData().getStatus());
                changeFlow(CURRENT_STATUS, rollback);
                getProviderHitCheck = 0;
//                h.removeCallbacks(r);
//                h.postDelayed(r, 0);
            }
            DATUM = dataResponse.getData();
        } else if (CURRENT_STATUS.equals(SERVICE) || CURRENT_STATUS.equals(MAP)) {
            System.out.println("RRR CURRENT_STATUS = " + CURRENT_STATUS);
        } else {
            Log.e("TRIP_INFO", "status clear - " + CURRENT_STATUS);
            CURRENT_STATUS = EMPTY;
            changeFlow(CURRENT_STATUS, false);
        }
        if (CURRENT_STATUS.equals(STARTED)
                || CURRENT_STATUS.equals(ARRIVED)
                || CURRENT_STATUS.equals(PICKED_UP)) {

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
        e.printStackTrace();
        try {
            throw new Throwable(e);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onPointError(Throwable e) {
        Log.e("wws", e.toString());
    }

    @Override
    public void onRouteError(Throwable e) {
        if (CURRENT_STATUS.equalsIgnoreCase(SERVICE) && mapFragment != null) {
            mapFragment.showRoute(null);
            Log.e("showRouteError", ">>>>>>>>>>>>>>>>>>");
        }
        Log.e("showRouteErrorOutside", e.getMessage());
    }

    public void addMarker(LatLng latLng) {
        mapFragment.addMarker(latLng);
    }


    @Override
    public void onSuccessRoute(SearchRoute o) {
        Log.d(TAG, "onSuccessRoute" + o.getPaths().size());
        if (CURRENT_STATUS.equalsIgnoreCase(SERVICE) || CURRENT_STATUS.equalsIgnoreCase(STARTED) && mapFragment != null) {
            mapFragment.showRoute(o);
            Log.e("showRouteSuccess", ">>>>>>>>>>>>>>>>>>");
        }
    }

    public void showError(int type) {
        switch (type) {
            case 1:
                errorMain.setText("Водитель не найден");
                error.setText("Рядом нет свободных машин");
                break;
            case 2:
                errorMain.setText("Все водидели заняты");
                error.setText("Ваша заявка отменена. Попробуйте позже.");
                break;
        }
        errorContainer.clearAnimation();
        errorContainer.setVisibility(View.VISIBLE);
        Animation fade_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        AnimationSet s = new AnimationSet(false);
        s.addAnimation(fade_out);
        s.setFillAfter(true);
        errorContainer.setAnimation(s);
    }

    @Override
    public void onSuccessProvers(List<Provider> providers) {
        Log.e("Providers", providers.toString());
        mapFragment.clearAllMarker();
        for (int i = 0; i < providers.size(); i++) {
            addMarker(new LatLng(providers.get(i).getLatitude(), providers.get(i).getLongitude()));
        }
    }

    @Override
    public void onErrorProviders(Throwable throwable) {
        Log.e("Provers", throwable.getMessage());
    }

    @Override
    public void onSuccessUpdate(CheckVersion checkVersion) {
        Log.e("checkVersion", checkVersion.toString());
        if (checkVersion.getForceUpdate()) {
            new ShowUpdateDialogFragment(checkVersion.getUrl()).show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onErrorUpdate(Throwable throwable) {

    }

    @Override
    public void onSuccessSearch(JsonArray array) {
        List<SearchAddress> addresses = new ArrayList<>();
        try {
            Log.e("SearchList", array.toString());
            for (int i = 0; i < array.size(); i++) {
                JsonObject object = (JsonObject) array.get(i);
                SearchAddress searchAddress = new SearchAddress();
                char singleQuotesChar = '"';
                searchAddress.setCoords(object.get("coords").toString());
                searchAddress.setMap(object.get("map").toString());
                searchAddress.setValue(object.get("value").toString().replace(singleQuotesChar, Character.MIN_VALUE));
                addresses.add(searchAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        emptyAddressAdapter.update(addresses.size() > 2 ? 0 : 1);
        searchAddressAdapter.update(addresses);
    }

    @Override
    public void onSuccessPoint(JsonObject o) {
        Log.e("onSuccess", o.toString());
        String value = o.get("name").toString();
        if (!value.equals("\", \"")) {
            SearchAddress searchAddress = new SearchAddress();
            searchAddress.setValue(value.substring(1, value.length() - 1));
            searchAddress.setCoords("(" + theLastLongitude + ", " + theLastLatitude + ")");
            if (CURRENT_STATUS.equals(EMPTY) && !isExpanded && o != null && !isDragging) {
                setStartAddress(searchAddress);
            }
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
    public void onSuccessCancelRequest(Object object) {
//        hideLoading();
        Log.e("onSuccessCancelRequest", "afkldfa");
        MvpApplication.RIDE_REQUEST.remove(DEST_ADD);
        MvpApplication.RIDE_REQUEST.remove(DEST_LAT);
        MvpApplication.RIDE_REQUEST.remove(DEST_LONG);
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        changeFlow(EMPTY, true);
    }

    @Override
    public void onErrorCancelRequest(Throwable e) {
//        hideLoading();
        Log.e("onErrorCancelRequest", "afkldfa");
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        changeFlow(EMPTY, true);
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

//    @Override
//    public void onSuccess(List<Provider> providerList) {
//        SharedHelper.putProviders(this, printJSON(providerList));
//    }

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
            this.changeFlow(RATING, false);
        }
    }

    // clicks

    @OnClick({R.id.on_map, R.id.sos, R.id.erase_src, R.id.erase_dest, R.id.btn_home, R.id.btn_work, R.id.menu_app, R.id.gps, R.id.source, R.id.destination, R.id.menu_back, R.id.doNotShowBetaVersion, R.id.form_dash})
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
            case R.id.form_dash:
                topLayout.transitionToEnd();
                break;
            case R.id.sos:
                SosFragment sosFragment = new SosFragment();
                sosFragment.show(getSupportFragmentManager(), SOS);
                break;
            case R.id.menu_back:
                topLayout.transitionToStart();
                Log.e("I am here", "really here");
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
                centerElement();
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
                    startActivity(new Intent(this, FavoritesActivity.class).putExtra(FavoritesActivity.TYPE, "home"));
                }
                break;
            case R.id.btn_work:
                if (work != null) {
                    updateSavedAddress(work);
                } else {
                    startActivity(new Intent(this, FavoritesActivity.class).putExtra(FavoritesActivity.TYPE, "work"));
                }
                break;
            case R.id.doNotShowBetaVersion:
                SharedHelper.putKey(getApplicationContext(), "doNotShowBetaVersion", true);
                ((RelativeLayout) findViewById(R.id.betaVersion)).setVisibility(View.GONE);
                break;
        }
    }

    public void centerElement() {
        if (mLastKnownLocation != null) {
            LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
            if (mapFragment != null) {
                mapFragment.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                        .target(currentLatLng)
                        .padding(
                                0.0,
                                0.0,
                                0.0,
                                getActualPadding()
                        )
                        .zoom(DEFAULT_ZOOM)
                        .build()));
            }
            isMapMoved = false;
        }
    }

    // drawer

    @Override
    public void onMenuClick(MenuDrawer menu) {
        if (menu instanceof MenuDrawer.MenuMain) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (menu instanceof MenuDrawer.MenuPayments) {
            startActivity(new Intent(this, PaymentActivity.class));
        } else if (menu instanceof MenuDrawer.MenuEarlyOrder) {
            Intent intent = new Intent(this, YourTripActivity.class);
            intent.putExtra("type", 2);
            startActivity(intent);
        } else if (menu instanceof MenuDrawer.MenuHistory) {
            Intent intent = new Intent(this, YourTripActivity.class);
            intent.putExtra("type", 1);
            startActivity(intent);
        } else if (menu instanceof MenuDrawer.MenuSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (menu instanceof MenuDrawer.MenuHelp) {
            startActivity(new Intent(this, HelpActivity.class));
        } else if (menu instanceof MenuDrawer.MenuFavorites) {
            startActivity(new Intent(this, FavoritesActivity.class));
        } else if (menu instanceof MenuDrawer.MenuNews) {
            startActivity(new Intent(this, NotificationManagerActivity.class));
        } else if (menu instanceof MenuDrawer.MenuDriver) {
            alertBecomeDriver();
        } else if (menu instanceof MenuDrawer.MenuInfo) {
            startActivity(new Intent(this, InfoActivity.class));
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
        lastPoint = point;
        if (CURRENT_STATUS.equalsIgnoreCase(EMPTY)) {

            isDragging = false;
            theLastLatitude = point.getLatitude();
            theLastLongitude = point.getLongitude();
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
        return getActualPadding();
    }

    public void clickToEditTextAndCloseBookMark(int i) {
        changeFlow("EMPTY", true);
        if (i == 1) {
            MvpApplication.RIDE_REQUEST.remove(DEST_ADD);
            MvpApplication.RIDE_REQUEST.remove(DEST_LAT);
            MvpApplication.RIDE_REQUEST.remove(DEST_LONG);
            destinationTxt.setText("");
            destinationTxt.setHint("Куда поедете?");
        } else {
            MvpApplication.RIDE_REQUEST.remove(SRC_ADD);
            MvpApplication.RIDE_REQUEST.remove(SRC_LAT);
            MvpApplication.RIDE_REQUEST.remove(SRC_LONG);
            sourceTxt.setText("");
            sourceTxt.setHint("Откуда поедете?");
        }
        CURRENT_STATUS = EMPTY;
        topLayout.enableTransition(R.id.tr, true);
        topLayout.transitionToStart();
    }
}
