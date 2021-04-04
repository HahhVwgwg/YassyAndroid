package com.thinkincab.app.ui.activity.favorites;

import com.thinkincab.app.base.BasePresenter;
import com.thinkincab.app.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FavoritesPresenter<V extends FavoritesIView> extends BasePresenter<V> implements FavoritesIPresenter<V> {

    @Override
    public void address() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .address()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void addAddress(HashMap<String, Object> params) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .addAddress(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessAddress, getMvpView()::onError));
    }

    @Override
    public void deleteAddress(Integer id, HashMap<String, Object> params) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .deleteAddress(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessAddress, getMvpView()::onError));
    }

}