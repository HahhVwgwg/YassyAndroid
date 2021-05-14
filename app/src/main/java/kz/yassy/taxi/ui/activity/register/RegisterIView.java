package kz.yassy.taxi.ui.activity.register;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.PhoneOtpResponse;
import kz.yassy.taxi.data.network.model.RegisterResponse;
import kz.yassy.taxi.data.network.model.SettingsResponse;

public interface RegisterIView extends MvpView {

    void onSuccess(SettingsResponse response);

    void onSuccess(RegisterResponse object);

    void onOtpSuccess(PhoneOtpResponse object);

    void onSuccess(Object object);

    void onSuccessPhoneNumber(Object object);

    void onVerifyPhoneNumberError(Throwable e);

    void onError(Throwable e);

    void onVerifyEmailError(Throwable e);
}
