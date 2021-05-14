package kz.yassy.taxi.ui.activity.splash;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import kz.yassy.taxi.base.BasePresenter;
import kz.yassy.taxi.data.network.APIClient;

public class SplashPresenter<V extends SplashIView> extends BasePresenter<V> implements SplashIPresenter<V> {

    @Override
    public void services() {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .services()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void profile() {
        try {
            getCompositeDisposable().add(APIClient
                    .getAPIClient()
                    .profile()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkVersion(HashMap<String, Object> map) {
        try {
            getCompositeDisposable().add(APIClient
                    .getAPIClient()
                    .checkversion(map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changeLanguage(String languageID) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .postChangeLanguage(languageID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onLanguageChanged, getMvpView()::onError));
    }
}
