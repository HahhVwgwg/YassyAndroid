package kz.yassy.taxi.ui.activity.card;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;

public class CardsPresenter<V extends CardsIView> extends
        BasePresenter<V> implements CarsIPresenter<V> {

    @Override
    public void card() {

        getCompositeDisposable().add(APIClient.getAPIClient().card()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(cards -> getMvpView().onSuccess(cards),
                        throwable -> getMvpView().onError(throwable)));
    }
}
