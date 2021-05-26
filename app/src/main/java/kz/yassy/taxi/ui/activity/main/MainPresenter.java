package kz.yassy.taxi.ui.activity.main;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.BuildConfig;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;
import kz.yassy.taxi.data.network.PlaceApiClient;
import kz.yassy.taxi.data.network.model.SearchAddress;
import retrofit2.http.FieldMap;

public class MainPresenter<V extends MainIView> extends BasePresenter<V> implements MainIPresenter<V> {

    public final static Map<String, SearchAddress> GEO_CACHE = new HashMap<>();

    private boolean enableProviders = true;
    private boolean enableCheckStatus = true;

    @Override
    public void getUserInfo() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .profile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void cancelRequest(@FieldMap HashMap<String, Object> params) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .cancelRequest(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessCancelRequest, getMvpView()::onErrorCancelRequest));
    }


    @Override
    public void payment(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .payment1(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(paymentResponse -> getMvpView().onSuccess(paymentResponse),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void checkStatus() {
        if (enableCheckStatus) {
            getCompositeDisposable().add(APIClient
                    .getAPIClient()
                    .checkStatus()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(getMvpView()::onSuccessCheckStatus, getMvpView()::onErrorCheckStatus));
        }
    }

    @Override
    public void logout(String id) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .logout(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessLogout, getMvpView()::onError));
    }

    @Override
    public void getProviders(HashMap<String, Object> params) {
        if (enableProviders) {
            getCompositeDisposable().add(APIClient
                    .getAPIClient()
                    .providers(params)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(getMvpView()::onSuccessProvers, getMvpView()::onErrorProviders));
        }
    }

    @Override
    public void getSavedAddress() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .address()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void getNavigationSettings() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .getSettings()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onSettingError));
    }

    @Override
    public void startSearch(String s) {
        getCompositeDisposable().add(PlaceApiClient
                .getAPIClient()
                .doPlaces(s)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onSuccessSearch, getMvpView()::onSearchError));

    }

    @Override
    public void startSearch(double lat, double lon) {
        HashMap<String, String> map = new HashMap<>();
        Log.e("lat,lon", lat + " " + lon);
        map.put("q", lat + "," + lon);
        getCompositeDisposable().add(PlaceApiClient
                .getAPIClient()
                .doPoint(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessPoint, getMvpView()::onPointError));
    }

    @Override
    public void checkUpdate() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", "user");
        map.put("device_type", BuildConfig.DEVICE_TYPE);
        map.put("version", BuildConfig.VERSION_NAME);
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .checkUpdate(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessUpdate, getMvpView()::onErrorUpdate));
    }

//    @Override
//    public void startSearch(double lat, double lon) {
//        if (GEO_CACHE.containsKey(lat + "," + lon)) {
//            getMvpView().onSuccessPoint(GEO_CACHE.get(lat + "," + lon));
//            return;
//        }
//        getCompositeDisposable().add(PlaceApiClient
//                .getAPIClient()
//                .doPoint(lat + "," + lon)
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(r -> {
//                    if (!GEO_CACHE.containsKey(lat + "," + lon)) {
//                        GEO_CACHE.put(lat + "," + lon, r);
//                    }
//                    getMvpView().onSuccessPoint(r);
//                }, getMvpView()::onPointError));
//    }

    @Override
    public void getRoute(double lat, double lon, double finishLat, double finishLon) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .doRoute("frontend", lat + "," + lon, finishLat + "," + finishLon)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                    getMvpView().onSuccessRoute(r);
                }, getMvpView()::onRouteError));
    }

    @Override
    public void updateDestination(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .extendTrip(obj)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onDestinationSuccess, getMvpView()::onError));
    }

}
