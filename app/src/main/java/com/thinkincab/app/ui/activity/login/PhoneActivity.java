package com.thinkincab.app.ui.activity.login;

import android.content.Intent;
import android.util.Log;

import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.iid.FirebaseInstanceId;
import com.thinkincab.app.BuildConfig;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseActivity;
import com.thinkincab.app.data.SharedHelper;
import com.thinkincab.app.data.network.model.ForgotResponse;
import com.thinkincab.app.data.network.model.OtpResponse;
import com.thinkincab.app.data.network.model.Token;
import com.thinkincab.app.data.network.model.TokenOtp;
import com.thinkincab.app.ui.activity.main.MainActivity;
import com.thinkincab.app.ui.adapter.LoginAdapter;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhoneActivity extends BaseActivity implements LoginIView {

    @BindView(R.id.slider)
    ViewPager2 slider;

    private LoginAdapter loginAdapter;

    private loginPresenter<PhoneActivity> presenter = new loginPresenter<>();

    private String phone = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_phone;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        loginAdapter = new LoginAdapter(this);
        slider.setUserInputEnabled(false);
        slider.setAdapter(loginAdapter);
        presenter.attachView(this);
    }

    public void onNext(String phone) {
        loginAdapter.getPhoneFragment().showLoading();
        this.phone = phone;
        presenter.sendPhone(phone.substring(1), "+7");
    }

    public void onComplete(String code) {
        loginAdapter.getCodeFragment().showLoading();
        if (SharedHelper.getKey(this, "device_token").isEmpty()) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    SharedHelper.putKey(this, "device_token", task.getResult().getToken());
                    Log.d("FCM_TOKEN", task.getResult().getToken());
                } else Log.w("PhoneActivity", "getInstanceId failed", task.getException());
            });
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("device_token", SharedHelper.getKey(this, "device_token", "No device"));
        map.put("device_id", SharedHelper.getKey(this, "device_id", "123"));
        map.put("device_type", BuildConfig.DEVICE_TYPE);
        map.put("otp", code);
        map.put("login_by", "whatsapp");
        map.put("first_name", "No");
        map.put("last_name", "Name");
        map.put("country_code", "7");
        map.put("mobile", phone.substring(1));
        map.put("email", phone.substring(1));
        presenter.loginByOtp(map);
    }

    @Override
    public void onSuccess(Token token) {


        //String accessToken = token.getTokenType() + " " + token.getAccessToken();
        //SharedHelper.putKey(this, "access_token", accessToken);
        //SharedHelper.putKey(this, "refresh_token", token.getRefreshToken());
        //SharedHelper.putKey(this, "logged_in", true);
        //finishAffinity();
        //startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSuccess(TokenOtp token) {
        loginAdapter.getCodeFragment().hideLoading();
        String accessToken = "Bearer " + token.getAccessToken();
        SharedHelper.putKey(this, "access_token", accessToken);
        //SharedHelper.putKey(this, "refresh_token", token.getRefreshToken());
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSuccess(ForgotResponse object) {

    }

    @Override
    public void onSuccess(OtpResponse object) {
        loginAdapter.getPhoneFragment().hideLoading();
        slider.setCurrentItem(1, false);
    }

    @Override
    public void onErrorCode(Throwable e) {
        loginAdapter.getCodeFragment().hideLoading();
        loginAdapter.getCodeFragment().onCodeError();
    }

    @Override
    public void onBackPressed() {
        if (slider.getCurrentItem() == 1) {
            slider.setCurrentItem(0, false);
            loginAdapter.getCodeFragment().clearCode();
        } else {
            super.onBackPressed();
        }
    }
}
