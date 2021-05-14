package kz.yassy.taxi.ui.activity.favorites;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;
import kz.yassy.taxi.data.network.PlaceApiClient;

public class FavoritesPresenter<V extends FavoritesIView> extends BasePresenter<V> implements FavoritesIPresenter<V> {

    @Override
    public void address() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .address()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void addAddress(HashMap<String, Object> params) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .addAddress(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessAddress, getMvpView()::onError));
    }

    @Override
    public void deleteAddress(Integer id, HashMap<String, Object> params) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .deleteAddress(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccessAddress, getMvpView()::onError));
    }

    @Override
    public void startSearch(String s) {
        getCompositeDisposable().add(PlaceApiClient
                .getAPIClient()
                .doPlaces(s)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMvpView()::onSuccessSearch, getMvpView()::onSearchError));

    }

}
