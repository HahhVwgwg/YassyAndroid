package com.thinkincab.app.ui.activity.main;

import com.thinkincab.app.base.BasePresenter;
import com.thinkincab.app.data.network.APIClient;
import com.thinkincab.app.data.network.PlaceApiClient;
import com.thinkincab.app.data.network.model.SearchAddress;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter<V extends MainIView> extends BasePresenter<V> implements MainIPresenter<V> {

    public final static Map<String, SearchAddress> GEO_CACHE = new HashMap<>();

    private boolean enableProviders = false;
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
                    .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
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
                    .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
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
                .doPlaces(s, "json", "kz", 1, 1, "67.3,44,69.6,42.7", "assylkhan.@c-link.kz")
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onSuccessSearch, getMvpView()::onSearchError));
    }

    @Override
    public void startSearch(double lat, double lon) {
        if (GEO_CACHE.containsKey(lat + "," + lon)) {
            getMvpView().onSuccessPoint(GEO_CACHE.get(lat + "," + lon));
            return;
        }
        getCompositeDisposable().add(PlaceApiClient
                .getAPIClient()
                .doPoint(lat, lon, "json", 1, 18, "assylkhan.@c-link.kz")
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                    if (!GEO_CACHE.containsKey(lat + "," + lon)) {
                        GEO_CACHE.put(lat + "," + lon, r);
                    }
                    getMvpView().onSuccessPoint(r);
                }, getMvpView()::onPointError));
    }

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
