package kz.yassy.taxi.ui.fragment.dispute;

import java.util.List;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.DisputeResponse;
import kz.yassy.taxi.data.network.model.Help;

public interface DisputeIView extends MvpView {

    void onSuccess(Object object);

    void onSuccessDispute(List<DisputeResponse> responseList);

    void onError(Throwable e);

    void onSuccess(Help help);
}
