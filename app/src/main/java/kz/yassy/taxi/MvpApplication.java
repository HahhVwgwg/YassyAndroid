package kz.yassy.taxi;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.mapbox.mapboxsdk.Mapbox;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import java.util.HashMap;

import kz.yassy.taxi.common.ConnectivityReceiver;
import kz.yassy.taxi.common.LocaleHelper;
import kz.yassy.taxi.data.network.model.Datum;

public class MvpApplication extends Application {

    public static String marker;
    private static MvpApplication mInstance;

    public static boolean canGoToChatScreen;
    public static boolean isChatScreenOpen;

    public static boolean isCash = true;
    public static boolean isCard;
    public static boolean isPayumoney;
    public static boolean isPaypal;
    public static boolean isPaytm;
    public static boolean isPaypalAdaptive;
    public static boolean isBraintree;

    //TODO ALLAN - Alterações débito na máquina e voucher
    public static boolean isDebitMachine;
    public static boolean isVoucher;

    public static HashMap<String, Object> RIDE_REQUEST = new HashMap<>();
    public static Datum DATUM = null;
    public static final int PICK_LOCATION_REQUEST_CODE = 3;
    public static boolean showOTP = true;

    public static boolean isCCAvenueEnabled = false;

    public static synchronized MvpApplication getInstance() {
        return mInstance;
    }

    public static MvpApplication getmInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
        MultiDex.install(newBase);
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        if (BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this);
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
        MultiDex.install(this);
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("5d1606b0-ba86-4b7c-a5ee-0117c5036d3e").build();
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this);
    }
}
