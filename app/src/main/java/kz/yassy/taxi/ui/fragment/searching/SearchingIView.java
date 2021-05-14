package kz.yassy.taxi.ui.fragment.searching;

import java.util.List;

import kz.yassy.taxi.base.MvpView;
import kz.yassy.taxi.data.network.model.Provider;

public interface SearchingIView extends MvpView {
    void onSuccess(Object object);

    void onError(Throwable e);

    void onSuccessProvider(List<Provider> objects);

    void onErrorProvider(Throwable e);
}
