package kz.yassy.taxi.ui.activity.past_trip_detail;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.PastTrip;
import kz.yassy.taxi.data.network.model.SearchRoute;

public interface PastTripDetailsIView extends MvpView {

    void onSuccess(PastTrip pastTrip);

    void onError(Throwable e);

    void onSuccessRoute(SearchRoute r);

    void onRouteError(Throwable throwable);
}
