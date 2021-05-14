package kz.yassy.taxi.ui.fragment.map_select;

import com.google.gson.JsonObject;

import kz.yassy.taxi.base.MvpView;

public interface MapSelectIView extends MvpView {
    void onSuccess(JsonObject object);

    void onError(Throwable e);
}
