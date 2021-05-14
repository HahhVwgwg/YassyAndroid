package kz.yassy.taxi.ui.activity.wallet;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;

public class WalletPresenter<V extends WalletIView> extends BasePresenter<V> implements WalletIPresenter<V> {

    @Override
    public void addMoney(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .addMoney(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void addMoneyPaytm(HashMap<String, Object> obj) {
        /*getCompositeDisposable().add(APIClient
                .getAPIClient()
                .addMoneyPaytm(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));*/
    }

    public void addMoneyRaz(String amount) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .addRazMoney(amount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::successs, getMvpView()::onError));
    }

    @Override
    public void getBrainTreeToken() {
        getCompositeDisposable().add(APIClient.getAPIClient().getBraintreeToken()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(response -> getMvpView().onSuccess(response),
                        throwable -> getMvpView().onError(throwable)));
    }


}
