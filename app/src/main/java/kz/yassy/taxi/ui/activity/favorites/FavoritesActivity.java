package kz.yassy.taxi.ui.activity.favorites;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.network.model.AddressResponse;
import kz.yassy.taxi.data.network.model.SearchAddress;
import kz.yassy.taxi.data.network.model.UserAddress;
import kz.yassy.taxi.ui.adapter.EmptyAddressAdapter;
import kz.yassy.taxi.ui.adapter.SearchAddressAdapter;
import kz.yassy.taxi.ui.utils.DisplayUtils;
import kz.yassy.taxi.ui.utils.ListOffset;

public class FavoritesActivity extends BaseActivity implements FavoritesIView {

    public static final String TYPE = "type.extra";

    @BindView(R.id.home_address)
    TextView homeAddress;
    @BindView(R.id.home_address_detail)
    TextView homeAddressDetail;
    @BindView(R.id.work_address)
    TextView workAddress;
    @BindView(R.id.work_address_detail)
    TextView workAddressDetail;
    @BindView(R.id.back_btn)
    View back;
    private static final long REQUEST_PLACES_DELAY = 1000;
    @BindView(R.id.destination)
    AppCompatEditText destinationTxt;
    @BindView(R.id.addresses)
    RecyclerView addressesList;

    private String type = "home";
    private final FavoritesPresenter<FavoritesActivity> presenter = new FavoritesPresenter<>();
    private UserAddress work = null, home = null;
    @BindView(R.id.otherAddress)
    RecyclerView otherAddressList;
    private BottomSheetBehavior bottomSheetBehavior;
    private SearchAddressAdapter searchAddressAdapter;
    private EmptyAddressAdapter emptyAddressAdapter;
    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {

        }
    };
    private final TextWatcher filterTextWatcher = new TextWatcher() {


        public void afterTextChanged(Editable editable) {
            Log.e("searchText", editable.toString());
            requestPlacesByDelay(editable);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };
    private SearchAddressAdapter otherAddressAdapter;

    private void requestPlacesByDelay(Editable s) {
        timer.cancel();
        timerTask.cancel();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
//                    if (isEditable && !s.toString().trim().isEmpty()) {
//                        mainPresenter.startSearch(s.toString());
//                    }
                    Log.e("startSearch", s.toString());
                    presenter.startSearch(s.toString());
                });
            }
        };
        timer.schedule(timerTask, REQUEST_PLACES_DELAY);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_favorites;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        LinearLayout bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        if (getIntent() != null && getIntent().getStringExtra(TYPE) != null) {
            type = getIntent().getStringExtra(TYPE);
            destinationTxt.setText("");
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            destinationTxt.requestFocus();
        }
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        hideKeyboard();
                        break;
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        Log.e("State", "STATE_HALF_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("State", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("State", "STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("State", "STATE_COLLAPSED");
                        break;
                }
            }

            @Override
            public void onSlide(View view, float v) {
                // do something when slide happens
            }
        });

        destinationTxt.addTextChangedListener(filterTextWatcher);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        emptyAddressAdapter = new EmptyAddressAdapter(1);
        searchAddressAdapter = new SearchAddressAdapter(new ArrayList<>(), item -> {
            Log.e("SearchList", item.toString());
            Intent intent = new Intent(this, FavoritesDetailsActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("address", item.getValue());
            intent.putExtra("latitude", item.getLat());
            intent.putExtra("longitude", item.getLon());
            destinationTxt.setText("");
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            startActivity(intent);
        });
        otherAddressAdapter = new SearchAddressAdapter(new ArrayList<>(), item -> {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.are_sure_you_want_to_delete))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(getString(R.string.delete), (dialog, whichButton) -> {
                        showLoading();
                        presenter.deleteAddress(Integer.valueOf(item.getCoords()), new HashMap<>());
                    }).setNegativeButton(getString(R.string.no), null).show();
        });
        addressesList.addItemDecoration(new ListOffset(DisplayUtils.dpToPx(24)));
        addressesList.setAdapter(searchAddressAdapter);
        otherAddressList.setAdapter(otherAddressAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        showLoading();
        presenter.address();
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @OnClick({R.id.home_btn, R.id.work_btn, R.id.add_btn, R.id.back_btn, R.id.on_map})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                type = "others";
                destinationTxt.setText("");
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                destinationTxt.requestFocus();
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.home_btn:
                if (home == null) {
                    type = "home";
                    destinationTxt.setText("");
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    destinationTxt.requestFocus();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.are_sure_you_want_to_delete))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(getString(R.string.delete), (dialog, whichButton) -> {
                                showLoading();
                                presenter.deleteAddress(home.getId(), new HashMap<>());
                            }).setNegativeButton(getString(R.string.no), null).show();
                }
                break;
            case R.id.work_btn:
                if (work == null) {
                    type = "work";
                    destinationTxt.setText("");
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    destinationTxt.requestFocus();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.are_sure_you_want_to_delete))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(getString(R.string.delete), (dialog, whichButton) -> {
                                showLoading();
                                presenter.deleteAddress(work.getId(), new HashMap<>());
                            }).setNegativeButton(getString(R.string.no), null).show();
                }
                break;
            case R.id.on_map:
                startActivity(new Intent(this, FavoritesSelectActivity.class).putExtra(TYPE, type));
                destinationTxt.setText("");
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
        }
    }

    private void showOwnKeyboard() {
        destinationTxt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(destinationTxt, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onSuccessAddress(Object object) {
        presenter.address();
    }

    @Override
    public void onSuccessSearch(JsonArray array) {
        List<SearchAddress> addresses = new ArrayList<>();
        try {
            Log.e("SearchList", array.toString());
            for (int i = 0; i < array.size(); i++) {
                JsonObject object = (JsonObject) array.get(i);
                SearchAddress searchAddress = new SearchAddress();
                char singleQuotesChar = '"';
                searchAddress.setCoords(object.get("coords").toString());
                searchAddress.setMap(object.get("map").toString());
                searchAddress.setValue(object.get("value").toString().replace(singleQuotesChar, Character.MIN_VALUE));
                addresses.add(searchAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchAddressAdapter.update(addresses);
        emptyAddressAdapter.update(addresses.size() > 2 ? 0 : 1);
    }

    @Override
    public void onSearchError(Throwable e) {

    }

    @Override
    public void onSuccess(AddressResponse address) {
        Log.e("afkldfaj;fa", address.toString());
        if (address.getHome().isEmpty()) {
            homeAddress.setText(R.string.add_home);
            homeAddressDetail.setText("");
            homeAddressDetail.setVisibility(View.GONE);
            home = null;
        } else {
            home = address.getHome().get(address.getHome().size() - 1);
            homeAddress.setText(R.string.home);
            homeAddressDetail.setText(home.getAddress());
            homeAddressDetail.setVisibility(View.VISIBLE);
        }
        if (address.getWork().isEmpty()) {
            workAddress.setText(R.string.add_work);
            workAddressDetail.setText("");
            workAddressDetail.setVisibility(View.GONE);
            work = null;
        } else {
            work = address.getWork().get(address.getWork().size() - 1);
            workAddress.setText(R.string.work);
            workAddressDetail.setText(work.getAddress());
            workAddressDetail.setVisibility(View.VISIBLE);
        }
        List<SearchAddress> addresses = new ArrayList<>();
        Log.e("Others", address.getOthers().toString());
        for (int i = 0; i < address.getOthers().size(); i++) {
            SearchAddress searchAddress = new SearchAddress();
            searchAddress.setCoords(String.valueOf(address.getOthers().get(i).getId()));
            searchAddress.setMap("");
            searchAddress.setValue(address.getOthers().get(i).getAddress());
            addresses.add(searchAddress);
        }
        Log.e("Others", addresses.toString());
        otherAddressAdapter.update(addresses);
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }
}
