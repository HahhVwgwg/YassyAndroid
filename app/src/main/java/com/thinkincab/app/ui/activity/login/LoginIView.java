package com.thinkincab.app.ui.activity.login;

import com.thinkincab.app.base.MvpView;
import com.thinkincab.app.data.network.model.ForgotResponse;
import com.thinkincab.app.data.network.model.OtpResponse;
import com.thinkincab.app.data.network.model.Token;
import com.thinkincab.app.data.network.model.TokenOtp;

public interface LoginIView extends MvpView {
    void onSuccess(Token token);

    void onSuccess(TokenOtp token);

    void onSuccess(ForgotResponse object);

    void onSuccess(OtpResponse object);

    void onError(Throwable e);

    void onErrorCode(Throwable e);
}
