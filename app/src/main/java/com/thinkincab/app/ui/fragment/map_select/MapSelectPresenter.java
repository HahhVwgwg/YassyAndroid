package com.thinkincab.app.ui.fragment.map_select;

import com.thinkincab.app.base.BasePresenter;
import com.thinkincab.app.data.network.PlaceApiClient;
import com.thinkincab.app.ui.activity.main.MainPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapSelectPresenter<V extends MapSelectIView> extends BasePresenter<V> implements MapSelectIPresenter<V> {

    @Override
    public void startSearch(double lat, double lon) {
        if (MainPresenter.GEO_CACHE.containsKey(lat + "," + lon)) {
            getMvpView().onSuccess(MainPresenter.GEO_CACHE.get(lat + "," + lon));
            return;
        }
        getCompositeDisposable().add(PlaceApiClient
                .getAPIClient()
                .doPoint(lat, lon, "json", 1, 18, "assylkhan.@c-link.kz")
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> {
                    if (!MainPresenter.GEO_CACHE.containsKey(lat + "," + lon)) {
                        MainPresenter.GEO_CACHE.put(lat + "," + lon, r);
                    }
                    getMvpView().onSuccess(r);
                }, getMvpView()::onError));
    }
}
