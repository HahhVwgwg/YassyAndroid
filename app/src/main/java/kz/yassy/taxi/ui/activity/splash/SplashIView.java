package kz.yassy.taxi.ui.activity.splash;

import java.util.List;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.CheckVersion;
import kz.yassy.taxi.data.network.model.Service;
import kz.yassy.taxi.data.network.model.User;

public interface SplashIView extends MvpView {

    void onSuccess(List<Service> serviceList);

    void onSuccess(User user);

    void onError(Throwable e);

    void onSuccess(CheckVersion checkVersion);
    void onLanguageChanged(Object object);
}
