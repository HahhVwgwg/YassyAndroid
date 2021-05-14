package kz.yassy.taxi.ui.activity.upcoming_trip_detail;

import kz.yassy.taxi.base.MvpPresenter;

public interface UpcomingTripDetailsIPresenter<V extends UpcomingTripDetailsIView> extends MvpPresenter<V> {

    void getUpcomingTripDetails(Integer requestId);
}
