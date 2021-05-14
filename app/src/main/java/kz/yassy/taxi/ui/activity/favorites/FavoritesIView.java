package kz.yassy.taxi.ui.activity.favorites;

import com.google.gson.JsonArray;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.AddressResponse;

public interface FavoritesIView extends MvpView {
    void onSuccess(AddressResponse address);

    void onSuccessAddress(Object object);

    void onSuccessSearch(JsonArray o);

    void onSearchError(Throwable e);
}
