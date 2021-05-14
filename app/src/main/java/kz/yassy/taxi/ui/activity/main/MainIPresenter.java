package kz.yassy.taxi.ui.activity.main;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

public interface MainIPresenter<V extends MainIView> extends MvpPresenter<V> {

    void getUserInfo();

    void logout(String id);

    void checkStatus();

    void payment(HashMap<String, Object> obj);

    void cancelRequest(HashMap<String, Object> params);

    void getSavedAddress();

    void getProviders(HashMap<String, Object> params);

    void getNavigationSettings();

    void startSearch(String s);

    void startSearch(double lat, double lon);

    void checkUpdate();

    void getRoute(double lat, double lon, double finishLat, double finishLon);

    void updateDestination(HashMap<String, Object> obj);

}
