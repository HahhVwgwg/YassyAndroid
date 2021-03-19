package com.thinkincab.app.ui.activity.main;

import com.thinkincab.app.base.MvpPresenter;

import java.util.HashMap;

public interface MainIPresenter<V extends MainIView> extends MvpPresenter<V> {

    void getUserInfo();

    void logout(String id);

    void checkStatus();

    void payment(HashMap<String, Object> obj);

    void getSavedAddress();

    void getProviders(HashMap<String, Object> params);

    void getNavigationSettings();

    void startSearch(String s);

    void startSearch(double lat, double lon);

    void getRoute(double lat, double lon, double finishLat, double finishLon);

    void updateDestination(HashMap<String, Object> obj);

}
