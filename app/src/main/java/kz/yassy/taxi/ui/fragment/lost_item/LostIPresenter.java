package kz.yassy.taxi.ui.fragment.lost_item;

import java.util.HashMap;

import kz.yassy.taxi.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface LostIPresenter<V extends LostIView> extends MvpPresenter<V> {
    void postLostItem(HashMap<String, Object> obj);
}
