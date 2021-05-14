package kz.yassy.taxi.ui.fragment.map_select;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.geometry.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseFragment;
import kz.yassy.taxi.data.network.model.SearchAddress;

import static kz.yassy.taxi.MvpApplication.RIDE_REQUEST;
import static kz.yassy.taxi.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_ADD;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LONG;

@SuppressLint("NonConstantResourceId")
public class MapSelectFragment extends BaseFragment implements MapSelectIView {

    @BindView(R.id.text_1)
    TextView text1;
    @BindView(R.id.text_2)
    TextView text2;

    private boolean isDragging = false;

    private SearchAddress current;
    private double thisLat;
    private double thisLong;

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
        thisLat = point.getLatitude();
        thisLong = point.getLongitude();
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
    public void onSuccess(JsonObject object) {
        String value = object.get("name").toString();
        if (!isDragging && !value.equals("\", \"")) {
            SearchAddress searchAddress = new SearchAddress();
            searchAddress.setValue(value.substring(1, value.length() - 1));
            current = searchAddress;
            text1.setText(searchAddress.getValue());
            text2.setText(searchAddress.getValue());
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
                RIDE_REQUEST.put(DEST_ADD, current.getValue());
                RIDE_REQUEST.put(DEST_LONG, thisLong);
                RIDE_REQUEST.put(DEST_LAT, thisLat);
                baseActivity().sendBroadcast(new Intent(INTENT_FILTER).putExtra("map", true));
            }
        }
    }
}
