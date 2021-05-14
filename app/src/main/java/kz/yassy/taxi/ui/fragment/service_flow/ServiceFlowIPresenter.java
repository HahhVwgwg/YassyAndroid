package kz.yassy.taxi.ui.fragment.service_flow;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

public interface ServiceFlowIPresenter<V extends ServiceFlowIView> extends MvpPresenter<V> {
    void checkStatus();

    void getPastTripDetails(Integer requestId);

    void getProviders(HashMap<String, Object> params);
}
