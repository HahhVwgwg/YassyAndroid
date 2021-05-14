package kz.yassy.taxi.ui.activity.passbook;

import kz.yassy.taxi.base.MvpPresenter;

public interface WalletHistoryIPresenter<V extends WalletHistoryIView> extends MvpPresenter<V> {
    void wallet();
}
