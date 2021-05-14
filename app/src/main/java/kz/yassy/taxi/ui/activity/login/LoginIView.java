package kz.yassy.taxi.ui.activity.login;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.ForgotResponse;
import kz.yassy.taxi.data.network.model.OtpResponse;
import kz.yassy.taxi.data.network.model.Token;
import kz.yassy.taxi.data.network.model.TokenOtp;

public interface LoginIView extends MvpView {
    void onSuccess(Token token);

    void onSuccess(TokenOtp token);

    void onSuccess(ForgotResponse object);

    void onSuccess(OtpResponse object);

    void onError(Throwable e);

    void onErrorCode(Throwable e);
}
