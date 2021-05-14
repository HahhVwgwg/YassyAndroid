package kz.yassy.taxi.ui.activity.register;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

public interface RegisterIPresenter<V extends RegisterIView> extends MvpPresenter<V> {

    void register(HashMap<String, Object> obj);

    void getSettings();

    void verifyEmail(String email);

    void verifyCredentials(String phoneNumber, String countryCode);

}
