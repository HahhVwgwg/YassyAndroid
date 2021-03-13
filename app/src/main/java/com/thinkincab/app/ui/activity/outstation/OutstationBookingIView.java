package com.thinkincab.app.ui.activity.outstation;

import com.thinkincab.app.base.MvpView;
import com.thinkincab.app.data.network.model.EstimateFare;
import com.thinkincab.app.ui.adapter.ServiceAdapterSingle;

public interface OutstationBookingIView extends MvpView {

    void onSuccess(ServiceAdapterSingle adapter);

    void onSuccessRequest(Object object);
    void onSuccess(EstimateFare estimateFare);
}
