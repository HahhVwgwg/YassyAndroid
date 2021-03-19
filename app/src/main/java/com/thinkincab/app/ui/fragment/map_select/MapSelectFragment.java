package com.thinkincab.app.ui.fragment.map_select;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseFragment;
import com.thinkincab.app.data.network.model.SearchAddress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.thinkincab.app.MvpApplication.RIDE_REQUEST;
import static com.thinkincab.app.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.DEST_ADD;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.DEST_LAT;
import static com.thinkincab.app.common.Constants.RIDE_REQUEST.DEST_LONG;

@SuppressLint("NonConstantResourceId")
public class MapSelectFragment extends BaseFragment implements MapSelectIView {

    @BindView(R.id.text_1)
    TextView text1;
    @BindView(R.id.text_2)
    TextView text2;

    private boolean isDragging = false;

    private SearchAddress current;

    private final MapSelectPresenter<MapSelectFragment> presenter = new MapSelectPresenter<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_map;
    }

    @Override
    protected View initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        return view;
    }

    public void onActionUp(LatLng point) {
        isDragging = false;
        presenter.startSearch(point.getLatitude(), point.getLongitude());
    }

    public void onActionDown() {
        isDragging = true;
        current = null;
        text1.setText("");
        text2.setText("");
    }

    // presenter

    @Override
    public void onSuccess(SearchAddress object) {
        if (!isDragging) {
            current = object;
            text1.setText(object.getShortAddress());
            text2.setText(object.getDisplayName());
        }
    }

    @Override
    public void onError(Throwable throwable) {
        super.onError(throwable);
    }

    // click

    @OnClick(R.id.select)
    public void onViewClicked(View view) {
        if (view.getId() == R.id.select) {
            if (current != null) {
                RIDE_REQUEST.put(DEST_ADD, current.getShortAddress());
                RIDE_REQUEST.put(DEST_LONG, current.getLon());
                RIDE_REQUEST.put(DEST_LAT, current.getLat());
                baseActivity().sendBroadcast(new Intent(INTENT_FILTER).putExtra("map", true));
            }
        }
    }
}
