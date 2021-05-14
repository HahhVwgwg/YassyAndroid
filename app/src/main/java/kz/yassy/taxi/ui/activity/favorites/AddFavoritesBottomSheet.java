package kz.yassy.taxi.ui.activity.favorites;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseDialogFragment;
import kz.yassy.taxi.data.network.model.AddressResponse;
import kz.yassy.taxi.data.network.model.SearchAddress;
import kz.yassy.taxi.ui.adapter.EmptyAddressAdapter;
import kz.yassy.taxi.ui.adapter.SearchAddressAdapter;
import kz.yassy.taxi.ui.utils.DisplayUtils;
import kz.yassy.taxi.ui.utils.ListOffset;

public class AddFavoritesBottomSheet extends BaseDialogFragment implements FavoritesIView {


    private static final long REQUEST_PLACES_DELAY = 1000;
    private final FavoritesPresenter<AddFavoritesBottomSheet> presenter = new FavoritesPresenter<>();
    @BindView(R.id.destination)
    AppCompatEditText destinationTxt;
    @BindView(R.id.addresses)
    RecyclerView addressesList;
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

    public AddFavoritesBottomSheet() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        View rootView = inflater.inflate(R.layout.fragment_add_address, container);

        //set to adjust screen height automatically, when soft keyboard appears on screen
        Window window = getDialog().getWindow();

        // set "origin" to top left corner, so to speak
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER);

        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 300;
        params.y = 100;
        window.setAttributes(params);

        Log.d("fafdas", String.format("Positioning DialogFragment to: x %d; y %d", params.x, params.y));
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_add_address;
    }

    private void requestPlacesByDelay(Editable s) {
        timer.cancel();
        timerTask.cancel();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_address, null);
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        destinationTxt.addTextChangedListener(filterTextWatcher);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager
                (getContext(), LinearLayoutManager.VERTICAL, false);
        emptyAddressAdapter = new EmptyAddressAdapter(1);
        searchAddressAdapter = new SearchAddressAdapter(new ArrayList<>(), item -> {
            Log.e("SearchList", item.toString());
//            'address' => 'required|max:255',
//                    'latitude' => 'required|numeric',
//                    'longitude' => 'required|numeric',
//                    'type' => 'required|in:home,work,recent,others'
//        ]
//            HashMap<String, Object> hashMap = new HashMap();
//            hashMap.put("address",item.getValue());
//            hashMap.put("latitude",item.getLat());
//            hashMap.put("longitude",item.getLon());
//            if (type == 1){
//                hashMap.put("type","home");
//            } else if (type == 2){
//                hashMap.put("type","work");
//            } else if (type == 3){
//                hashMap.put("type","others");
//            }
//            presenter.addAddress(hashMap);
        });
        ConcatAdapter adapter = new ConcatAdapter(searchAddressAdapter, emptyAddressAdapter);
        addressesList.addItemDecoration(new ListOffset(DisplayUtils.dpToPx(24)));

        addressesList.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        Dialog dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        return dialog;
    }

    @Override
    public View initView(View view) {
        return view;
    }


    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

//    @OnClick(R.id.submit)
//    public void onViewClicked() {
//    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void onSuccess(AddressResponse address) {

    }

    @Override
    public void onSuccessAddress(Object object) {
        Log.e("SuccessUpload", object.toString());
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
}
