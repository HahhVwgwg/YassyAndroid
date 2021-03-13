package com.thinkincab.app.ui.activity.outstation;

import android.widget.Toast;

import com.thinkincab.app.R;
import com.thinkincab.app.base.BasePresenter;
import com.thinkincab.app.data.network.APIClient;
import com.thinkincab.app.data.network.model.EstimateFare;
import com.thinkincab.app.data.network.model.Service;
import com.thinkincab.app.ui.adapter.ServiceAdapterSingle;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OutstationBookingPresenter<V extends OutstationBookingIView> extends BasePresenter<V> implements OutstationBookingIPresenter<V> {
    @Override
    public void services() {

        Observable modelObservable = APIClient.getAPIClient().services();

        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> {
                            ServiceAdapterSingle adapter = new ServiceAdapterSingle(getMvpView().baseActivity(), (List<Service>) trendsResponse);
                            getMvpView().onSuccess(adapter);
                        },
                        (Consumer) throwable -> getMvpView().onError((Throwable) throwable));
    }

    @Override
    public void sendRequest(HashMap<String, Object> params) {
        Observable modelObservable = APIClient.getAPIClient().sendRequest(params);
        getMvpView().showLoading();
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> {
                            getMvpView().hideLoading();
                            Toast.makeText(activity(), R.string.new_outstation_request_created, Toast.LENGTH_SHORT).show();
                            OutstationBookingPresenter.this.getMvpView().onSuccessRequest((Object) trendsResponse);
                        },
                        throwable -> {
                            getMvpView().hideLoading();
                            OutstationBookingPresenter.this.getMvpView().onError((Throwable) throwable);
                        });
    }

    @Override
    public void estimateFare(HashMap<String, Object> params) {
        getMvpView().showLoading();
        Observable modelObservable =  APIClient.getAPIClient().estimateFare2(params);
        modelObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(trendsResponse -> {
                            getMvpView().hideLoading();
                            OutstationBookingPresenter.this.getMvpView().onSuccess((EstimateFare) trendsResponse);
                        },
                        (Consumer) throwable -> {
                            getMvpView().hideLoading();
                            OutstationBookingPresenter.this.getMvpView().onError((Throwable) throwable);
                        });
    }
}