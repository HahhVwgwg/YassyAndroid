package kz.yassy.taxi.ui.fragment.searching;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.mapbox.mapboxsdk.geometry.LatLng;

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
import kz.yassy.taxi.data.network.model.Datum;
import kz.yassy.taxi.data.network.model.Provider;
import kz.yassy.taxi.ui.activity.main.MainActivity;

import static kz.yassy.taxi.MvpApplication.DATUM;
import static kz.yassy.taxi.MvpApplication.RIDE_REQUEST;
import static kz.yassy.taxi.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_ADD;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.DEST_LONG;
import static kz.yassy.taxi.common.Constants.Status.EMPTY;

public class SearchingFragment extends BaseFragment implements SearchingIView {

    @BindView(R.id.search)
    RippleSearchView search;
    private Runnable runnable;
    private Handler handler;

    private SearchingPresenter<SearchingFragment> presenter = new SearchingPresenter<>();

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
            map.put("latitude", RIDE_REQUEST.get(DEST_LAT));
            map.put("longitude", RIDE_REQUEST.get(DEST_LONG));
            presenter.getProviders(map);
            ((MainActivity) Objects.requireNonNull(getActivity())).centerElement();
        }
        return view;
    }

    @Override
    public void onSuccessProvider(List<Provider> objects) {
        Log.e("Providers", objects.toString());
        handler = new Handler();
        runnable = () -> {
            try {
//                ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(new LatLng(43.20787215, 76.66984601),new LatLng(objects.get(0).getLatitude(),objects.get(0).getLongitude()));
                ((MainActivity) Objects.requireNonNull(getActivity())).addMarker(new LatLng(objects.get(0).getLatitude(), objects.get(0).getLongitude()));
//                ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(new LatLng(43.20787215,76.66984601),new LatLng(objects.get(0).getLatitude(),objects.get(0).getLongitude()));
                handler.postDelayed(runnable, 10000);
            } catch (Exception e) {
                handler.postDelayed(runnable, 100);
                e.printStackTrace();
            }
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

        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(EMPTY, true);
    }

    @Override
    public void onError(Throwable e) {
//        handleError(e);
        ((MainActivity) Objects.requireNonNull(getActivity())).showError(2);
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow(EMPTY, true);
    }


}
