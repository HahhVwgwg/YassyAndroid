package kz.yassy.taxi.ui.activity.register;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;

public class RegisterPresenter<V extends RegisterIView>
        extends BasePresenter<V>
        implements RegisterIPresenter<V> {

    @Override
    public void register(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .register(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    public void otp(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .registerOtp(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onOtpSuccess, getMvpView()::onError));
    }

    @Override
    public void verifyEmail(String email) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .verifyEmail(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onVerifyEmailError));
    }

    @Override
    public void verifyCredentials(String phoneNumber, String countryCode) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .verifyCredentials(phoneNumber, countryCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessPhoneNumber, getMvpView()::onVerifyPhoneNumberError));
    }

    @Override
    public void getSettings() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .getSettings()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
