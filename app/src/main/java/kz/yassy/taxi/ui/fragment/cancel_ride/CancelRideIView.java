package kz.yassy.taxi.ui.fragment.cancel_ride;

import java.util.List;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.CancelResponse;

public interface CancelRideIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);

    void onSuccess(List<CancelResponse> response);

    void onReasonError(Throwable e);
}
