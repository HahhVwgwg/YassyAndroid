package kz.yassy.taxi.ui.fragment.searching;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.turf.TurfMeasurement;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.MvpApplication;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseFragment;
import kz.yassy.taxi.common.RippleSearchView;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.model.Datum;
import kz.yassy.taxi.data.network.model.Provider;
import kz.yassy.taxi.ui.activity.main.MainActivity;

import static kz.yassy.taxi.MvpApplication.DATUM;
import static kz.yassy.taxi.MvpApplication.RIDE_REQUEST;
import static kz.yassy.taxi.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_ADD;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LONG;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LONG;
import static kz.yassy.taxi.common.Constants.Status.EMPTY;

public class SearchingFragment extends BaseFragment implements SearchingIView {

    private final SearchingPresenter<SearchingFragment> presenter = new SearchingPresenter<>();
    private Runnable runnable;
    private Handler handler;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.search)
    RippleSearchView search;
    private int index = 0;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_searching;
    }

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        if (DATUM != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("latitude", RIDE_REQUEST.get(SRC_LAT));
            map.put("longitude", RIDE_REQUEST.get(SRC_LONG));
            ((MainActivity) Objects.requireNonNull(getActivity())).centerElementCustom(new LatLng(DATUM.getSLatitude(), DATUM.getSLongitude()), 11);
            presenter.getProviders(map);
        }
        return view;
    }

    @Override
    public void onSuccessProvider(List<Provider> objects) {
        Log.e("Providers", objects.toString());
        handler = new Handler();
        runnable = () -> {
            if (index >= objects.size()) {
                handler.removeCallbacks(runnable);
                Log.e("indexMine", "return ");
                return;
            }
            int distance = ((int) TurfMeasurement.distance(Point.fromLngLat(objects.get(index).getLongitude(), objects.get(index).getLatitude()), Point.fromLngLat(DATUM.getSLongitude(), DATUM.getSLatitude()), "meters"));
            int zoom;
            Log.e("indexMine", " " + index);
            if (distance > 2700)
                zoom = 11;
            else if (distance < 1200)
                zoom = 13;
            else
                zoom = 12;
            Log.e("indexMine", "before");
            ((MainActivity) Objects.requireNonNull(getActivity())).centerElementCustom(new LatLng(DATUM.getSLatitude(), DATUM.getSLongitude()), zoom);
            ((MainActivity) Objects.requireNonNull(getActivity())).drawRouteWithoutTaxiAnimation(new LatLng(objects.get(index).getLatitude(), objects.get(index).getLongitude()), new LatLng(DATUM.getSLatitude(), DATUM.getSLongitude()));
            Log.e("indexMine", "after");
            index++;
            handler.postDelayed(runnable, 4000);
        };
        handler.postDelayed(runnable, 100);
    }

    @Override
    public void onErrorProvider(Throwable e) {
        e.printStackTrace();
        if (handler != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        search.startRippleAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        search.stopRippleAnimation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDetach();
        index = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) handler.removeCallbacks(runnable);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (handler != null) handler.removeCallbacks(runnable);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.cancel)
    public void onViewClicked() {
        alertCancel();
    }

    private void alertCancel() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.are_sure_you_want_to_cancel_the_request)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    if (DATUM != null) {
                        showLoading();
                        Datum datum = DATUM;
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("request_id", datum.getId());
                        presenter.cancelRequest(map);
                    }
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    @Override
    public void onSuccess(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        MvpApplication.RIDE_REQUEST.remove(DEST_ADD);
        MvpApplication.RIDE_REQUEST.remove(DEST_LAT);
        MvpApplication.RIDE_REQUEST.remove(DEST_LONG);
        SharedHelper.putKey(Objects.requireNonNull(getContext()), "cancelRideActivity", true);
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(EMPTY, true);
    }

    @Override
    public void onError(Throwable e) {
        ((MainActivity) Objects.requireNonNull(getActivity())).showError(2);
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(EMPTY, true);
    }


}
