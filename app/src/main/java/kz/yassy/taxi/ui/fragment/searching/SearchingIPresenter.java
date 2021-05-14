package kz.yassy.taxi.ui.fragment.searching;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;
import retrofit2.http.FieldMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SearchingIPresenter<V extends SearchingIView> extends MvpPresenter<V> {
    void cancelRequest(@FieldMap HashMap<String, Object> params);

    void getProviders(HashMap<String, Object> params);
}
