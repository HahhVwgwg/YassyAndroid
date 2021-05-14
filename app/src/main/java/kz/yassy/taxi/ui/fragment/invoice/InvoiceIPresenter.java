package kz.yassy.taxi.ui.fragment.invoice;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

public interface InvoiceIPresenter<V extends InvoiceIView> extends MvpPresenter<V> {
    void payment(HashMap<String, Object> obj);

    void updateRide(HashMap<String, Object> obj);

    void payuMoneyChecksum();

    void paytmCheckSum(String request_id, String paymentmode);

    void getBrainTreeToken();

    void updatePayment(HashMap<String, Object> obj);


}
