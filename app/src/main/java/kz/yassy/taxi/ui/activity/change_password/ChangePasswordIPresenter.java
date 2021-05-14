package kz.yassy.taxi.ui.activity.change_password;


import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;


public interface ChangePasswordIPresenter<V extends ChangePasswordIView> extends MvpPresenter<V> {
    void changePassword(HashMap<String, Object> parms);
}
