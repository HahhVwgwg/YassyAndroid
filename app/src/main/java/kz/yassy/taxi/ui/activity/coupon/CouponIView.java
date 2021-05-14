package kz.yassy.taxi.ui.activity.coupon;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.PromoResponse;

public interface CouponIView extends MvpView {
    void onSuccess(PromoResponse object);

    void onError(Throwable e);
}
