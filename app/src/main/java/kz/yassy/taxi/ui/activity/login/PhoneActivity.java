package kz.yassy.taxi.ui.activity.login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.yassy.taxi.BuildConfig;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.model.ForgotResponse;
import kz.yassy.taxi.data.network.model.OtpResponse;
import kz.yassy.taxi.data.network.model.Token;
import kz.yassy.taxi.data.network.model.TokenOtp;
import kz.yassy.taxi.ui.activity.main.MainActivity;
import kz.yassy.taxi.ui.adapter.LoginAdapter;
import kz.yassy.taxi.ui.countrypicker.Country;

public class PhoneActivity extends BaseActivity implements LoginIView {

    @BindView(R.id.slider)
    ViewPager2 slider;

    private LoginAdapter loginAdapter;
    private final String LAST_PHONE_NUMBER = "LAST_PHONE_NUMBER";

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
        SharedHelper.putKey(getApplicationContext(), "showOnBoarding", false);
    }

    public void onNext(String phone) {
        this.phone = phone;
        if (SharedHelper.getKey(getApplicationContext(), LAST_PHONE_NUMBER, "-1").equals(phone))
            slider.setCurrentItem(1, false);
        else {
            loginAdapter.getPhoneFragment().showLoading();
            Country country = getDeviceCountry(getApplicationContext());
            SharedHelper.putKey(getApplicationContext(), LAST_PHONE_NUMBER, phone);
            initialState();
            presenter.sendPhone(phone, country.getDialCode());
        }
    }

    public void onNextFromCodeFragment() {
        Country country = getDeviceCountry(getApplicationContext());
        presenter.sendPhone(phone, country.getDialCode());
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
        Country country = getDeviceCountry(getApplicationContext());
        map.put("otp", code);
        map.put("login_by", "whatsapp");
        map.put("first_name", "");
        map.put("last_name", "");
        map.put("country_code", country.getDialCode().substring(1));
        map.put("mobile", phone);
        map.put("email", phone);
        presenter.loginByOtp(map);
    }

    protected Country getDeviceCountry(Context context) {
        return Country.getCountryByName(SharedHelper.getKey(context, "countrySelected", "Kazakhstan"));
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
        SharedHelper.putKey(getApplicationContext(), LAST_PHONE_NUMBER, "-1");
        initialState();
    }

    private void initialState() {
        SharedHelper.putKey(getApplicationContext(), "TIMER_SECONDS", 80000);
        SharedHelper.putKey(getApplicationContext(), "BAN_COUNT", 0);
        SharedHelper.putKey(getApplicationContext(), "BAN_TIME", 0L);
    }

    @Override
    public void onSuccess(ForgotResponse object) {

    }

    @Override
    public void onSuccess(OtpResponse object) {
        loginAdapter.getPhoneFragment().hideLoading();
        slider.setCurrentItem(1, false);
        if (object.getStatus().equals("whatsapp"))
            loginAdapter.getCodeFragment().setWhatsAppSms();
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

