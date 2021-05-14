package kz.yassy.taxi.ui.activity.forgot_password;


import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ForgotPasswordIPresenter<V extends ForgotPasswordIView> extends MvpPresenter<V> {
    void resetPassword(HashMap<String, Object> parms);
}
