package kz.yassy.taxi.ui.fragment.map_select;

import kz.yassy.taxi.base.MvpPresenter;

public interface MapSelectIPresenter<V extends MapSelectIView> extends MvpPresenter<V> {
    void startSearch(double lat, double lon);
}
