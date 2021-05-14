package kz.yassy.taxi.ui.activity.wallet;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

public interface WalletIPresenter<V extends WalletIView> extends MvpPresenter<V> {
    void addMoney(HashMap<String, Object> obj);

    void addMoneyPaytm(HashMap<String, Object> obj);

    void getBrainTreeToken();
}
