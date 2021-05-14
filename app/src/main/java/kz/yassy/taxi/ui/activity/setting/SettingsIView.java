package kz.yassy.taxi.ui.activity.setting;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.AddressResponse;

public interface SettingsIView extends MvpView {

    void onSuccessAddress(Object object);

    void onLanguageChanged(Object object);

    void onSuccess(AddressResponse address);

    void onError(Throwable e);
}
