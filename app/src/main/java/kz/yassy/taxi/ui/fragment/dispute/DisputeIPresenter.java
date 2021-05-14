package kz.yassy.taxi.ui.fragment.dispute;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

public interface DisputeIPresenter<V extends DisputeIView> extends MvpPresenter<V> {

    void help();

    void dispute(HashMap<String, Object> obj);

    void getDispute();
}
