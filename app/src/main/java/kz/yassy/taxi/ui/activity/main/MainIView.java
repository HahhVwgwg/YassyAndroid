package kz.yassy.taxi.ui.activity.main;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.AddressResponse;
import kz.yassy.taxi.data.network.model.CheckVersion;
import kz.yassy.taxi.data.network.model.DataResponse;
import kz.yassy.taxi.data.network.model.Message;
import kz.yassy.taxi.data.network.model.Provider;
import kz.yassy.taxi.data.network.model.SearchRoute;
import kz.yassy.taxi.data.network.model.SettingsResponse;
import kz.yassy.taxi.data.network.model.User;

public interface MainIView extends MvpView {

    void onSuccess(User user);

    void onSuccessCancelRequest(Object object);

    void onErrorCancelRequest(Throwable e);

    void onSuccess(DataResponse dataResponse);

    void onSuccessCheckStatus(DataResponse dataResponse);

    void onDestinationSuccess(Object object);

    void onSuccess(Message message);

    void onSuccessLogout(Object object);

    void onSuccess(AddressResponse response);

    void onError(Throwable e);

    void onErrorCheckStatus(Throwable e);

    void onSettingError(Throwable e);

    void onSearchError(Throwable e);

    void onPointError(Throwable e);

    void onRouteError(Throwable e);

    void onSuccess(SettingsResponse response);

    void onSuccessSearch(JsonArray o);

    void onSuccessPoint(JsonObject o);

    void onSuccessRoute(SearchRoute o);

    void onSuccessProvers(List<Provider> providers);

    void onErrorProviders(Throwable throwable);

    void onSuccessUpdate(CheckVersion checkVersion);

    void onErrorUpdate(Throwable throwable);
}
