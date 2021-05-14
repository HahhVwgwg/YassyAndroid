package kz.yassy.taxi.ui.activity.favorites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.mapbox.mapboxsdk.geometry.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.network.model.Datum;
import kz.yassy.taxi.data.network.model.SearchAddress;
import kz.yassy.taxi.ui.fragment.map.IMapView;
import kz.yassy.taxi.ui.fragment.map.MapFragment;
import kz.yassy.taxi.ui.fragment.map_select.MapSelectIView;
import kz.yassy.taxi.ui.fragment.map_select.MapSelectPresenter;

import static kz.yassy.taxi.ui.activity.favorites.FavoritesActivity.TYPE;

public class FavoritesSelectActivity extends BaseActivity implements IMapView, MapSelectIView {

    @BindView(R.id.text_1)
    TextView text1;
    @BindView(R.id.text_2)
    TextView text2;
    private Datum datum;
    private MapFragment mapFragment;
    private boolean isDragging = false;
    private SearchAddress current;
    private double thisLat;
    private double thisLong;
    private String type = "home";

    private MapSelectPresenter<FavoritesSelectActivity> presenter = new MapSelectPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_favorites_show_on_map;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        ButterKnife.bind(this);
        presenter.attachView(this);
        mapFragment = new MapFragment();
        if (getIntent() != null && getIntent().getStringExtra(TYPE) != null) {
            type = getIntent().getStringExtra(TYPE);
        }
        getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();

    }

    @OnClick({R.id.select, R.id.back_btn})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.select) {
            if (current != null) {
                Intent intent = new Intent(this, FavoritesDetailsActivity.class);
                intent.putExtra("type", type);
                intent.putExtra("address", current.getValue());
                intent.putExtra("latitude", thisLat);
                intent.putExtra("longitude", thisLong);
                startActivity(intent);
                finish();
            }
        } else {
            finish();
        }
    }

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
    public void onError(Throwable e) {
        handleError(e);
    }


    @Override
    public void onMapMoved(boolean moved) {
        Log.e("Workign", "onMapMoved");
    }

    @Override
    public void onActionUp(LatLng point) {

        Log.e("Workign", "onActionUp");
        isDragging = false;
        thisLat = point.getLatitude();
        thisLong = point.getLongitude();
        presenter.startSearch(point.getLatitude(), point.getLongitude());
    }

    @Override
    public void onActionDown() {
        Log.e("Workign", "onActionDown");
        isDragging = true;
        current = null;
        text1.setText("");
        text2.setText("");
    }

    @Override
    public void onMapReady() {
        Log.e("Workign", "onMapReady");
    }

    @Override
    public int getMapPadding() {
        return 0;
    }
}
