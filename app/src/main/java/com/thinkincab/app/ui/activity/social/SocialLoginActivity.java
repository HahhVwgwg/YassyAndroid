package com.thinkincab.app.ui.activity.social;

import android.content.Intent;
import android.view.View;

import com.thinkincab.app.BuildConfig;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseActivity;
import com.thinkincab.app.data.SharedHelper;
import com.thinkincab.app.data.network.model.Token;
import com.thinkincab.app.ui.activity.main.MainActivity;

import java.util.HashMap;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SocialLoginActivity extends BaseActivity implements SocialIView {

    private SocialPresenter<SocialLoginActivity> presenter = new SocialPresenter<>();
    private HashMap<String, Object> map = new HashMap<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_social_login;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);

        map.put("device_token", SharedHelper.getKey(this, "device_token"));
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("device_type", BuildConfig.DEVICE_TYPE);

    }

//    @OnClick({R.id.facebook, R.id.google})
    @OnClick({R.id.facebook})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.facebook:
                fbLogin();
                break;
//            case R.id.google:
//                showLoading();
//                startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_GOOGLE_LOGIN);
//                break;
        }
    }

    void fbLogin() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void register() {
        map.put("mobile", SharedHelper.getKey(this, "mobile"));
        map.put("country_code", SharedHelper.getKey(this, "country_code"));
        if (Objects.equals(map.get("login_by"), "google")) presenter.loginGoogle(map);
        else if (Objects.equals(map.get("login_by"), "facebook")) presenter.loginFacebook(map);

        showLoading();
    }

    @Override
    public void onSuccess(Token token) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String accessToken = token.getTokenType() + " " + token.getAccessToken();
        SharedHelper.putKey(this, "access_token", accessToken);
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
