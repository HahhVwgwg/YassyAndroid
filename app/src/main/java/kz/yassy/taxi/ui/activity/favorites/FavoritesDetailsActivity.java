package kz.yassy.taxi.ui.activity.favorites;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.network.model.AddressResponse;

public class FavoritesDetailsActivity extends BaseActivity implements FavoritesIView {

    public static final String TYPE = "type";
    private final FavoritesPresenter<FavoritesDetailsActivity> presenter = new FavoritesPresenter<>();
    @BindView(R.id.back_btn)
    View back;
    @BindView(R.id.home_address)
    TextView homeAddress;
    @BindView(R.id.type)
    EditText typeET;
    @BindView(R.id.additionalAddress)
    EditText additionalAddress;
    @BindView(R.id.delete)
    TextView delete;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.imageType)
    ImageView imageType;
    private String type = "home";
    private boolean enable = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_favorites_details;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        if (getIntent() != null) {
            type = getIntent().getStringExtra(TYPE);
            homeAddress.setText(getIntent().getStringExtra("address"));
            if (type.equals("home")) {
                typeET.setText("Дом");
                imageType.setImageResource(R.drawable.ic_home_orange);
            } else if (type.equals("work")) {
                typeET.setText("Работа");
                imageType.setImageResource(R.drawable.ic_work_orange);
            } else if (type.equals("others")) {
                typeET.setText("Другие");
                imageType.setImageResource(R.drawable.ic_pin_map_color);
            }
            // todo
        }
        typeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

//                Log.e("Editable",editable + " ");
//                Log.e("Editable",typeET.getText() + " " + additionalAddress.getText());
//                submit.setEnabled(editable.length() > 0 && additionalAddress.getText().length() > 0);
            }
        });
        additionalAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                Log.e("Editable",editable + " ");
//                Log.e("Editable",typeET.getText() + " " + additionalAddress.getText());
//                submit.setEnabled(editable.length() > 0 && typeET.getText().length() > 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @OnClick({R.id.back_btn, R.id.submit, R.id.delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.delete:
                new AlertDialog.Builder(this)
                        .setMessage("Вы действительно хотите удалить адрес?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Да", (dialog, whichButton) -> {
                            finish();
                        }).setNegativeButton("Нет", null).show();
                break;
            case R.id.submit:
                HashMap<String, Object> hashMap = new HashMap();
                hashMap.put("address", homeAddress.getText());
                hashMap.put("latitude", getIntent().getDoubleExtra("latitude", 0));
                hashMap.put("longitude", getIntent().getDoubleExtra("longitude", 0));
                hashMap.put("type", type);
                presenter.addAddress(hashMap);
                showLoading();
                break;
        }
    }

    @Override
    public void onSuccessAddress(Object object) {
//        presenter.address();
        Log.e("success", "uraaaa");
        hideLoading();
        finish();
    }

    @Override
    public void onSuccessSearch(JsonArray o) {

    }

    @Override
    public void onSearchError(Throwable e) {

    }

    @Override
    public void onSuccess(AddressResponse address) {

    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }
}
