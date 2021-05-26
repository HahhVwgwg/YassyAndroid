package kz.yassy.taxi.ui.fragment.book_ride;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;


public interface BookRideIPresenter<V extends BookRideIView> extends MvpPresenter<V> {
    void rideNow(HashMap<String, Object> obj);

    void getCouponList();

    void services();

    void profile();
}
