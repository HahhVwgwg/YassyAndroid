package kz.yassy.taxi.ui.fragment.schedule;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ScheduleIPresenter<V extends ScheduleIView> extends MvpPresenter<V> {
    void sendRequest(HashMap<String, Object> obj);
}
