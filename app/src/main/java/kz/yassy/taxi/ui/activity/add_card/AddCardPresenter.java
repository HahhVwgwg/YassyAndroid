package kz.yassy.taxi.ui.activity.add_card;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;


public class AddCardPresenter<V extends AddCardIView> extends BasePresenter<V> implements AddCardIPresenter<V> {
    @Override
    public void card(String cardId) {

        getCompositeDisposable().add(APIClient.getAPIClient().card(cardId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }
}
