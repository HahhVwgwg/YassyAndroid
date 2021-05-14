package kz.yassy.taxi.ui.fragment.invoice;

//import com.appoets.paytmpayment.PaytmObject;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.BrainTreeResponse;
import kz.yassy.taxi.data.network.model.CheckSumData;
import kz.yassy.taxi.data.network.model.Message;

public interface InvoiceIView extends MvpView {
    void onSuccess(Message message);

    void onSuccess(Object o);

    void onSuccessPayment(Object o);

    void onError(Throwable e);

    void onSuccess(BrainTreeResponse response);

    void onPayumoneyCheckSumSucess(CheckSumData checkSumData);

   // void onPayTmCheckSumSucess(PaytmObject payTmResponse);
}
