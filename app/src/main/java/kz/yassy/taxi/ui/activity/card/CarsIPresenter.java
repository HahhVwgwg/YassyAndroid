package kz.yassy.taxi.ui.activity.card;

import kz.yassy.taxi.base.MvpPresenter;


public interface CarsIPresenter<V extends CardsIView> extends MvpPresenter<V> {
    void card();
}
