package kz.yassy.taxi.ui.activity.wallet;

//..import com.appoets.paytmpayment.PaytmObject;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.AddWallet;
import kz.yassy.taxi.data.network.model.BrainTreeResponse;

public interface WalletIView extends MvpView {
    void onSuccess(AddWallet object);

   // void onSuccess(PaytmObject object);
    void successs(AddWallet object);
    void onSuccess(BrainTreeResponse response);
    void onError(Throwable e);
}
