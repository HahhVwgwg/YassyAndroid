package com.thinkincab.app.ui.activity.login;

import com.thinkincab.app.base.BasePresenter;
import com.thinkincab.app.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class loginPresenter<V extends LoginIView> extends BasePresenter<V>
        implements LoginIPresenter<V> {
    @Override
    public void login(HashMap<String, Object> obj) {

        getCompositeDisposable().add(APIClient.getAPIClient().login(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(token -> getMvpView().onSuccess(token),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void loginByOtp(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient.getAPIClient().loginByOtp(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(token -> getMvpView().onSuccess(token),
                        throwable -> getMvpView().onErrorCode(throwable)));
    }

    @Override
    public void forgotPassword(String email) {

        getCompositeDisposable().add(APIClient.getAPIClient().forgotPassword(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void sendPhone(String phone, String code) {
        getCompositeDisposable().add(APIClient.getAPIClient().requestCode(phone, code)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }
}
