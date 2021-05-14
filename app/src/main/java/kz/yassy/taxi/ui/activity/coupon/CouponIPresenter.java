package kz.yassy.taxi.ui.activity.coupon;

import kz.yassy.taxi.base.MvpPresenter;

public interface CouponIPresenter<V extends CouponIView> extends MvpPresenter<V> {
    void coupon();
}
