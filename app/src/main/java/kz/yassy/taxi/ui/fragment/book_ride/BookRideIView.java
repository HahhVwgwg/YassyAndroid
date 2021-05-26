package kz.yassy.taxi.ui.fragment.book_ride;

import java.util.List;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.PromoResponse;
import kz.yassy.taxi.data.network.model.Service;
import kz.yassy.taxi.data.network.model.User;


public interface BookRideIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);

    void onSuccessCoupon(PromoResponse promoResponse);

    void onSuccess(List<Service> serviceList);

    void onSuccess(User user);
}
