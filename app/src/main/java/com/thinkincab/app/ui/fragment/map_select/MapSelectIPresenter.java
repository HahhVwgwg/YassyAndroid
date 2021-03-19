package com.thinkincab.app.ui.fragment.map_select;

import com.thinkincab.app.base.MvpPresenter;

public interface MapSelectIPresenter<V extends MapSelectIView> extends MvpPresenter<V> {
    void startSearch(double lat, double lon);
}
