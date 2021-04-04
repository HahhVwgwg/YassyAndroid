package com.thinkincab.app.ui.activity.favorites;

import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseActivity;
import com.thinkincab.app.data.network.model.AddressResponse;
import com.thinkincab.app.data.network.model.UserAddress;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private String type = "home";
    private final FavoritesPresenter<FavoritesActivity> presenter = new FavoritesPresenter<>();
    private UserAddress work = null, home = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_favorites;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        setTitle(getString(R.string.favorites));
        showLoading();
        presenter.address();
        if (getIntent() != null && getIntent().getStringExtra(TYPE) != null) {
            type = getIntent().getStringExtra(TYPE);
            // todo
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @OnClick({R.id.home_btn, R.id.work_btn, R.id.add_btn, R.id.back_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.home_btn:
                if (home == null) {
                    type = "home";
                    //Intent intent = new Intent(this, LocationPickActivity.class);
                    //intent.putExtra("actionName", Constants.LocationActions.SELECT_HOME);
                    // startActivityForResult(intent, REQUEST_PICK_LOCATION);
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
                    // Intent workIntent = new Intent(this, LocationPickActivity.class);
                    // workIntent.putExtra("actionName", Constants.LocationActions.SELECT_WORK);
                    // startActivityForResult(workIntent, REQUEST_PICK_LOCATION);
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
        }
    }

    @Override
    public void onSuccessAddress(Object object) {
        presenter.address();
    }

    @Override
    public void onSuccess(AddressResponse address) {
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
