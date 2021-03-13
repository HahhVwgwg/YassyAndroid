package com.thinkincab.app.ui.fragment.book_ride;

import com.thinkincab.app.base.MvpView;
import com.thinkincab.app.data.network.model.PromoResponse;
import com.thinkincab.app.data.network.model.Service;

import java.util.List;


public interface BookRideIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);

    void onSuccessCoupon(PromoResponse promoResponse);
    void onSuccess(List<Service> serviceList);
}
