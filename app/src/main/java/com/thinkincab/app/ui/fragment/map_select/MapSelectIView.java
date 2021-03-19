package com.thinkincab.app.ui.fragment.map_select;

import com.thinkincab.app.base.MvpView;
import com.thinkincab.app.data.network.model.SearchAddress;

public interface MapSelectIView extends MvpView {
    void onSuccess(SearchAddress object);

    void onError(Throwable e);
}
