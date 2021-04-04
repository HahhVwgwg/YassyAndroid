package com.thinkincab.app.ui.activity.favorites;

import com.thinkincab.app.base.MvpView;
import com.thinkincab.app.data.network.model.AddressResponse;

public interface FavoritesIView extends MvpView {
    void onSuccess(AddressResponse address);
    void onSuccessAddress(Object object);
}
