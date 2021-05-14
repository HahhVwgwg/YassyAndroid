package kz.yassy.taxi.ui.fragment.upcoming_trip;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface UpcomingTripIPresenter<V extends UpcomingTripIView> extends MvpPresenter<V> {
    void upcomingTrip();

    void cancelRequest(HashMap<String, Object> params);
}
