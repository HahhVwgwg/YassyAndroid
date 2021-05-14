package kz.yassy.taxi.ui.fragment.service_flow;

import java.util.List;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.DataResponse;
import kz.yassy.taxi.data.network.model.PastTrip;
import kz.yassy.taxi.data.network.model.Provider;

public interface ServiceFlowIView extends MvpView {
    void onSuccess(DataResponse dataResponse);

    void onSuccessTripDetails(PastTrip pastTrip);

    void onSuccessProvider(List<Provider> objects);

    void onErrorProvider(Throwable e);
}
