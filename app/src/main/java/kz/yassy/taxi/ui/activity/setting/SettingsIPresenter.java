package kz.yassy.taxi.ui.activity.setting;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

public interface SettingsIPresenter<V extends SettingsIView> extends MvpPresenter<V> {
    void addAddress(HashMap<String, Object> params);

    void deleteAddress(Integer id, HashMap<String, Object> params);

    void address();

    void changeLanguage(String languageID);
}
