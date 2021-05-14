package kz.yassy.taxi.ui.activity.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.common.LocaleHelper;
import kz.yassy.taxi.data.network.model.AddressResponse;
import kz.yassy.taxi.data.network.model.UserAddress;
import kz.yassy.taxi.ui.activity.main.MainActivity;

import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_ADD;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LAT;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SRC_LONG;

public class SettingsActivity extends BaseActivity implements SettingsIView {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.cacheClear)
    RelativeLayout cacheClear;


    private String type = "home";
    private String language;
    private SettingsPresenter<SettingsActivity> presenter = new SettingsPresenter<>();
    private UserAddress work = null, home = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings_mine;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);

        // Activity title will be updated after the locale has changed in Runtime
//        setTitle(getString(R.string.settings));
//        showLoading();
//        presenter.address();

//        languageReset();

//        chooseLanguage.setOnCheckedChangeListener((group, checkedId) -> {
//            showLoading();
//            switch (checkedId) {
//                case R.id.english:
//                    language = Constants.Language.ENGLISH;
//                    break;
//                case R.id.arabic:
//                    language = Constants.Language.ARABIC;
//                    break;
//            }
//            presenter.changeLanguage(language);
//
//        });
    }

//    private void languageReset() {
//        String dd = LocaleHelper.getLanguage(getApplicationContext());
//        switch (dd) {
//            case Constants.Language.ENGLISH:
//                english.setChecked(true);
//                break;
//            case Constants.Language.ARABIC:
//                arabic.setChecked(true);
//                break;
//            default:
//                english.setChecked(true);
//                break;
//        }
//    }

    @Override
    public void onSuccessAddress(Object object) {
        presenter.address();
    }

    @Override
    public void onLanguageChanged(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        LocaleHelper.setLocale(getApplicationContext(), language);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK));
        this.overridePendingTransition(R.anim.rotate_in, R.anim.rotate_out);
    }

    @Override
    public void onSuccess(AddressResponse address) {

    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    public void deleteCache() {
        try {
            File dir = this.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @OnClick({R.id.back_btn, R.id.cacheClear, R.id.languages})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cacheClear:
                new AlertDialog.Builder(this)
                        .setMessage("Загруженные фрагменты, карты " +
                                "будут удалены. При следующем " +
                                "использовании карта будет " +
                                "загружена заново?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Очистить", (dialog, whichButton) -> {
                            deleteCache();
                        }).setNegativeButton("Нет", null).show();
                break;
            case R.id.languages:
                startActivity(new Intent(this, SettingsLanguageActivity.class));
                break;
            case R.id.back_btn:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_LOCATION) if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                showLoading();
                HashMap<String, Object> map = new HashMap<>();
                map.put("type", type);
                map.put("address", data.getStringExtra(SRC_ADD));
                map.put("latitude", data.getDoubleExtra(SRC_LAT, 0.0));
                map.put("longitude", data.getDoubleExtra(SRC_LONG, 0.0));
                presenter.addAddress(map);
            }

        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}