package com.thinkincab.app.ui.activity.outstation;

import com.thinkincab.app.base.MvpPresenter;

import java.util.HashMap;

public interface OutstationBookingIPresenter<V extends OutstationBookingIView> extends MvpPresenter<V> {
    void services();
    void sendRequest(HashMap<String, Object> params);
    void estimateFare(HashMap<String, Object> params);
}
