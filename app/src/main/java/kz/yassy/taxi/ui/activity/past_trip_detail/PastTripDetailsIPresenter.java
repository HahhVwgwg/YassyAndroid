package kz.yassy.taxi.ui.activity.past_trip_detail;

import kz.yassy.taxi.base.MvpPresenter;

public interface PastTripDetailsIPresenter<V extends PastTripDetailsIView> extends MvpPresenter<V> {

    void getPastTripDetails(Integer requestId);

    void getRoute(double lat, double lon, double finishLat, double finishLon);
}
