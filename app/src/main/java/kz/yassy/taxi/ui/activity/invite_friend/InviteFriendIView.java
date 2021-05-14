package kz.yassy.taxi.ui.activity.invite_friend;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.User;

public interface InviteFriendIView extends MvpView {

    void onSuccess(User user);

    void onError(Throwable e);

}
