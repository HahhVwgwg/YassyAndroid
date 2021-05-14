package kz.yassy.taxi.ui.activity.social;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;


public class SocialPresenter<V extends SocialIView> extends BasePresenter<V> implements SocialIPresenter<V> {
    @Override
    public void loginGoogle(HashMap<String, Object> obj) {

        getCompositeDisposable().add(APIClient.getAPIClient().loginGoogle(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(token -> getMvpView().onSuccess(token),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void loginFacebook(HashMap<String, Object> obj) {

        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .loginFacebook(obj)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        trendsResponse -> getMvpView().onSuccess(trendsResponse),
                        throwable -> getMvpView().onError(throwable)
                ));
    }

}
