package kz.yassy.taxi.ui.activity.login;

import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.BuildConfig;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.model.ForgotResponse;
import kz.yassy.taxi.data.network.model.OtpResponse;
import kz.yassy.taxi.data.network.model.Token;
import kz.yassy.taxi.data.network.model.TokenOtp;
import kz.yassy.taxi.ui.activity.forgot_password.ForgotPasswordActivity;
import kz.yassy.taxi.ui.activity.main.MainActivity;
import kz.yassy.taxi.ui.activity.register.RegisterActivity;

public class EmailActivity extends BaseActivity  implements LoginIView {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.pass)
    EditText pass;
    @BindView(R.id.toolbar)
    ImageView mToolbar;
    public static String TAG = "";
    private loginPresenter<EmailActivity> presenter = new loginPresenter();
    @Override
    public int getLayoutId() {
        return R.layout.activity_email;
    }

    private HashMap<String, Object> map = new HashMap<>();

    @Override
    public void initView() {
        ButterKnife.bind(this);


        map.put("device_token", SharedHelper.getKey(this, "device_token"));
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("device_type", BuildConfig.DEVICE_TYPE);

        //((TextInputLayout)findViewById(R.id.textInputLayout)).setHint("Phone number");

        presenter.attachView(this);
        mToolbar.setOnClickListener(v -> finish());


         findViewById(R.id.imageView2).setOnClickListener(view -> {

             ((TextInputLayout)findViewById(R.id.textInputLayout)).setHint("Email");
             ((EditText)findViewById(R.id.email)).setText("");
             ((ImageView)findViewById(R.id.imageView)).setBackgroundResource(R.drawable.button_round_white);
             ((ImageView)findViewById(R.id.imageView2)).setBackgroundResource(R.drawable.button_round_accent);
             ((EditText)findViewById(R.id.email)).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
         });
         findViewById(R.id.imageView).setOnClickListener(view -> {
             ((TextInputLayout)findViewById(R.id.textInputLayout)).setHint("Phone number");
             ((EditText)findViewById(R.id.email)).setText("");
             ((ImageView)findViewById(R.id.imageView)).setBackgroundResource(R.drawable.button_round_accent);
             ((ImageView)findViewById(R.id.imageView2)).setBackgroundResource(R.drawable.button_round_white);
             ((EditText)findViewById(R.id.email)).setInputType(InputType.TYPE_CLASS_PHONE); ;
         });

        //if (BuildConfig.DEBUG) email.setText("User@demo.com");
    }


    @OnClick({R.id.sign_up, R.id.next,R.id.forgot_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.forgot_password:
                showLoading();
                presenter.forgotPassword(email.getText().toString());
                break;
            case R.id.sign_up:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.next:
                String Email = email.getText().toString();
                if (email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                    return;
                }
                login();
                break;
        }
    }

    private void login() {
        try {
            if (pass.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }
            if (SharedHelper.getKey(this, "device_token").isEmpty()) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SharedHelper.putKey(this, "device_token", task.getResult().getToken());
                        Log.d("FCM_TOKEN", task.getResult().getToken());
                    } else Log.w("PasswordActivity", "getInstanceId failed", task.getException());
                });
                return;
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("grant_type", "password");
            map.put("username", email.getText().toString());
            map.put("password",pass.getText().toString() );
            map.put("client_secret", BuildConfig.CLIENT_SECRET);
            map.put("client_id", BuildConfig.CLIENT_ID);
            map.put("device_token", SharedHelper.getKey(this, "device_token", "No device"));
            map.put("device_id", SharedHelper.getKey(this, "device_id", "123"));
            map.put("device_type", BuildConfig.DEVICE_TYPE);

            showLoading();
            presenter.login(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        SharedHelper.putKey(this, "refresh_token", token.getRefreshToken());
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void onSuccess(TokenOtp token) {

    }

    @Override
    public void onSuccess(ForgotResponse forgotResponse) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Toast.makeText(this, forgotResponse.getMessage(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("email", forgotResponse.getEmail());
        intent.putExtra("otp", forgotResponse.getOtp());
        intent.putExtra("id", forgotResponse.getId());
        startActivity(intent);
    }

    @Override
    public void onSuccess(OtpResponse object) {

    }

    @Override
    public void onError(Throwable e) {
        TAG = "PasswordActivity";
        handleError(e);
    }

    @Override
    public void onErrorCode(Throwable e) {

    }
}
