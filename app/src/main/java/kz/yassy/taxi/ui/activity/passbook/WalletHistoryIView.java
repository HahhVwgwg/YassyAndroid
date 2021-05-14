package kz.yassy.taxi.ui.activity.passbook;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.WalletResponse;

public interface WalletHistoryIView extends MvpView {
    void onSuccess(WalletResponse response);

    void onError(Throwable e);
}
