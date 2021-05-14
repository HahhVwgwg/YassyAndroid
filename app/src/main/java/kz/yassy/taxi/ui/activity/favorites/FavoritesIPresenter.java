package kz.yassy.taxi.ui.activity.favorites;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

public interface FavoritesIPresenter<V extends FavoritesIView> extends MvpPresenter<V> {
    void address();

    void addAddress(HashMap<String, Object> params);

    void deleteAddress(Integer id, HashMap<String, Object> params);

    void startSearch(String toString);
}
