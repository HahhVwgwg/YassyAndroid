package kz.yassy.taxi.ui.activity.social;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.Token;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIView extends MvpView {
    void onSuccess(Token token);

    void onError(Throwable e);
}
