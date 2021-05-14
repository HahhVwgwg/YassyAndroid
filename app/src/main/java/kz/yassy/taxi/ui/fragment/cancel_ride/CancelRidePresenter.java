package kz.yassy.taxi.ui.fragment.cancel_ride;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;
import retrofit2.http.FieldMap;

public class CancelRidePresenter<V extends CancelRideIView> extends BasePresenter<V> implements CancelRideIPresenter<V> {

    @Override
    public void cancelRequest(@FieldMap HashMap<String, Object> params) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .cancelRequest(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void getReasons() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .getCancelReasons()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onReasonError));
    }
}
