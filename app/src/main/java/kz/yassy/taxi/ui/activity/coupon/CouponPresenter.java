package kz.yassy.taxi.ui.activity.coupon;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;

public class CouponPresenter<V extends CouponIView> extends BasePresenter<V> implements CouponIPresenter<V> {

    @Override
    public void coupon() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .promocodesList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
