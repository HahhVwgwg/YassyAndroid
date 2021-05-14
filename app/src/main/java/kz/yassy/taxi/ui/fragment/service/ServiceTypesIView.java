package kz.yassy.taxi.ui.fragment.service;

import kz.yassy.taxi.base.MvpView;

public interface ServiceTypesIView extends MvpView {

    void onError(Throwable e);

    void onSuccess(Object object);
}
