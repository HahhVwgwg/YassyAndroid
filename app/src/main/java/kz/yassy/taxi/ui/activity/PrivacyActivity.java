package kz.yassy.taxi.ui.activity;

import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.yassy.taxi.BuildConfig;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;

public class PrivacyActivity extends BaseActivity {

    @BindView(R.id.web_view)
    WebView webView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_privacy;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        webView.loadUrl(BuildConfig.BASE_URL + "privacy");

    }
}
