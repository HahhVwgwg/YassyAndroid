package kz.yassy.taxi.ui.activity.social;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIPresenter<V extends SocialIView> extends MvpPresenter<V> {
    void loginGoogle(HashMap<String, Object> obj);

    void loginFacebook(HashMap<String, Object> obj);
}
