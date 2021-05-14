package kz.yassy.taxi.ui.activity.splash;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

public interface SplashIPresenter<V extends SplashIView> extends MvpPresenter<V> {

    void services();

    void profile();

    void checkVersion(HashMap<String, Object> map);

    void changeLanguage(String languageID);
}
