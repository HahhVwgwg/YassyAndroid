package kz.yassy.taxi.ui.fragment.service;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

public interface ServiceTypesIPresenter<V extends ServiceTypesIView> extends MvpPresenter<V> {

    void rideNow(HashMap<String, Object> obj);

}
