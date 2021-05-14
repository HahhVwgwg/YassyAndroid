package kz.yassy.taxi.ui.activity.upcoming_trip_detail;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;

public class UpcomingTripDetailsPresenter<V extends UpcomingTripDetailsIView> extends BasePresenter<V>
        implements UpcomingTripDetailsIPresenter<V> {

    @Override
    public void getUpcomingTripDetails(Integer requestId) {

        getCompositeDisposable().add(APIClient.getAPIClient().upcomingTripDetails(requestId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(data -> getMvpView().onSuccess(data),
                        throwable -> getMvpView().onError(throwable)));
    }
}
