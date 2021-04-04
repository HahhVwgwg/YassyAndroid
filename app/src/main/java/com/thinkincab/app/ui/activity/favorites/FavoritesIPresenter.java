package com.thinkincab.app.ui.activity.favorites;

import com.thinkincab.app.base.MvpPresenter;

import java.util.HashMap;

public interface FavoritesIPresenter<V extends FavoritesIView> extends MvpPresenter<V> {
    void address();
    void addAddress(HashMap<String, Object> params);
    void deleteAddress(Integer id, HashMap<String, Object> params);
}
