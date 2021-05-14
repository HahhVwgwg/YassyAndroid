package kz.yassy.taxi.ui.fragment.map_select;

import android.util.Log;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.PlaceApiClient;

public class MapSelectPresenter<V extends MapSelectIView> extends BasePresenter<V> implements MapSelectIPresenter<V> {

    @Override
    public void startSearch(double lat, double lon) {
//        if (MainPresenter.GEO_CACHE.containsKey(lat + "," + lon)) {
//            getMvpView().onSuccess(MainPresenter.GEO_CACHE.get(lat + "," + lon));
//            return;
//        }
        HashMap<String, String> map = new HashMap<>();
        Log.e("lat,lon", lat + " " + lon);
        map.put("q", lat + "," + lon);
        getCompositeDisposable().add(PlaceApiClient
                .getAPIClient()
                .doPoint(map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }
}
