package kz.yassy.taxi.ui.activity.help;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.Help;

public interface HelpIView extends MvpView {

    void onSuccess(Help help);

    void onError(Throwable e);
}
