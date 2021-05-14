package kz.yassy.taxi.ui.fragment.rate;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

public interface RatingIPresenter<V extends RatingIView> extends MvpPresenter<V> {

    void rate(HashMap<String, Object> obj);
}
