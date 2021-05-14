package kz.yassy.taxi.ui.activity.profile;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.User;

public interface ProfileIView extends MvpView {

    void onSuccess(User user);

    void onUpdateSuccess(User user);

    void onError(Throwable e);

    void onSuccessPhoneNumber(Object object);

    void onVerifyPhoneNumberError(Throwable e);
}
