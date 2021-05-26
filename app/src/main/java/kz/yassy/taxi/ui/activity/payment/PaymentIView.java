package kz.yassy.taxi.ui.activity.payment;

//import com.appoets.paytmpayment.PaytmObject;

import java.util.List;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.BrainTreeResponse;
import kz.yassy.taxi.data.network.model.Card;
import kz.yassy.taxi.data.network.model.CheckSumData;
import kz.yassy.taxi.data.network.model.User;

public interface PaymentIView extends MvpView {

    void onSuccess(Object card);

    void onSuccess(BrainTreeResponse response);

    void onSuccess(List<Card> cards);

    void onAddCardSuccess(Object cards);

    void onError(Throwable e);

    void onSuccess(User user);

    void onPayumoneyCheckSumSucess(CheckSumData checkSumData);

    //void onPayTmCheckSumSuccess(PaytmObject payTmResponse);

}
