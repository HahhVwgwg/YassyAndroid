package kz.yassy.taxi.ui.activity.setting;

import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;

public class SettingsLanguageActivity extends BaseActivity {

    @BindView(R.id.back_btn)
    ImageView backBtn;

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings_languages;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back_btn)
    public void onViewClicked(View view) {
        if (view.getId() == R.id.back_btn) {
            finish();
        }
    }
}