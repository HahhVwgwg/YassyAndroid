package kz.yassy.taxi.ui.activity.past_trip_detail;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;

public class PastTripDetailsPresenter<V extends PastTripDetailsIView> extends BasePresenter<V>
        implements PastTripDetailsIPresenter<V> {

    @Override
    public void getPastTripDetails(Integer requestId) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .pastTripDetails(requestId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void getRoute(double lat, double lon, double finishLat, double finishLon) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .doRoute("frontend", lat + "," + lon, finishLat + "," + finishLon)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> getMvpView().onSuccessRoute(r), getMvpView()::onRouteError));
    }
}
