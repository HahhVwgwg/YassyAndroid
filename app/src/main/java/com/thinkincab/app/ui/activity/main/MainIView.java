package com.thinkincab.app.ui.activity.main;

import com.thinkincab.app.base.MvpView;
import com.thinkincab.app.data.network.model.AddressResponse;
import com.thinkincab.app.data.network.model.DataResponse;
import com.thinkincab.app.data.network.model.Message;
import com.thinkincab.app.data.network.model.Provider;
import com.thinkincab.app.data.network.model.SearchAddress;
import com.thinkincab.app.data.network.model.SearchRoute;
import com.thinkincab.app.data.network.model.SettingsResponse;
import com.thinkincab.app.data.network.model.User;

import java.util.List;

public interface MainIView extends MvpView {

    void onSuccess(User user);

    void onSuccess(DataResponse dataResponse);

    void onDestinationSuccess(Object object);
    void onSuccess(Message message);

    void onSuccessLogout(Object object);

    void onSuccess(AddressResponse response);

    void onSuccess(List<Provider> objects);

    void onError(Throwable e);

    void onSettingError(Throwable e);

    void onSearchError(Throwable e);

    void onPointError(Throwable e);

    void onSuccess(SettingsResponse response);

    void onSuccessSearch(List<SearchAddress> o);

    void onSuccessPoint(SearchAddress o);

    void onSuccessRoute(SearchRoute o);
}
