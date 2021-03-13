package com.thinkincab.app.ui.activity.main;

import com.thinkincab.app.base.BasePresenter;
import com.thinkincab.app.data.network.APIClient;
import com.thinkincab.app.data.network.PlaceApiClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter<V extends MainIView> extends BasePresenter<V> implements MainIPresenter<V> {

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
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .checkStatus()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
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
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .providers(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
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
                .doPlaces(s, "json", "kz", 1, "assylkhan.@c-link.kz")
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onSuccessSearch, getMvpView()::onSettingError));
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
