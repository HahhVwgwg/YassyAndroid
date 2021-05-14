package kz.yassy.taxi.ui.activity.login;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface LoginIPresenter<V extends LoginIView> extends MvpPresenter<V> {
    void login(HashMap<String, Object> obj);

    void loginByOtp(HashMap<String, Object> obj);

    void forgotPassword(String email);

    void sendPhone(String phone, String code);
}
