package com.thinkincab.app.ui.activity.login;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.thinkincab.app.BuildConfig;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseActivity;
import com.thinkincab.app.data.SharedHelper;
import com.thinkincab.app.data.network.model.ForgotResponse;
import com.thinkincab.app.data.network.model.OtpResponse;
import com.thinkincab.app.data.network.model.Token;
import com.thinkincab.app.data.network.model.TokenOtp;
import com.thinkincab.app.ui.activity.forgot_password.ForgotPasswordActivity;
import com.thinkincab.app.ui.activity.main.MainActivity;
import com.thinkincab.app.ui.activity.register.RegisterActivity;
import com.thinkincab.app.ui.activity.social.SocialLoginActivity;
import com.thinkincab.app.ui.countrypicker.Country;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

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

    private GoogleSignInClient mGoogleSignInClient;
    @Override
    public void initView() {
        ButterKnife.bind(this);


        map.put("device_token", SharedHelper.getKey(this, "device_token"));
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("device_type", BuildConfig.DEVICE_TYPE);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_signin_server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



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

    void fbLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    map.put("login_by", "facebook");
                    map.put("accessToken", loginResult.getAccessToken().getToken());
                    Country mCountry = getDeviceCountry(EmailActivity.this);
                    fbOtpVerify(mCountry.getCode(), mCountry.getDialCode(), "");
                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
                String s = exception.getMessage();
                if (exception instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null)
                        LoginManager.getInstance().logOut();
                } else if (s.contains("GraphQLHttpFailureDomain"))
                    Toasty.info(EmailActivity.this, getString(R.string.fb_session_expired), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private CallbackManager callbackManager;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_GOOGLE_LOGIN) {
                try {
                    hideLoading();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                String TAG = "REQUEST_GOOGLE_LOGIN";
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    //String token = account.getIdToken();
                    map.put("login_by", "google");
                    Runnable runnable = () -> {
                        try {
                            String scope = "oauth2:" + Scopes.EMAIL + " " + Scopes.PROFILE;
                            String accessToken = GoogleAuthUtil.getToken(getApplicationContext(), Objects.requireNonNull(Objects.requireNonNull(account).getAccount()), scope, new Bundle());
                            Log.d(TAG, "accessToken:" + accessToken);
                            map.put("accessToken", accessToken);
                            Country mCountry = getDeviceCountry(EmailActivity.this);
                            fbOtpVerify(mCountry.getCode(), mCountry.getDialCode(), "");
                        } catch (IOException | GoogleAuthException e) {
                            e.printStackTrace();
                        }
                    };
                    AsyncTask.execute(runnable);

                } catch (ApiException e) {
                    Log.w(TAG, "signInResult:failed code = " + e.getStatusCode());
                }
            } else if (requestCode == REQUEST_ACCOUNT_KIT && data != null) {
                AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
                if (!loginResult.wasCancelled())
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                            Log.d("REQUEST_ACCOUNT_KIT", "onSuccess: Account Kit" + Objects.requireNonNull(AccountKit.getCurrentAccessToken()).getToken());
                            if (Objects.requireNonNull(AccountKit.getCurrentAccessToken()).getToken() != null) {
                                PhoneNumber phoneNumber = account.getPhoneNumber();
                                SharedHelper.putKey(EmailActivity.this, "country_code", "+" + phoneNumber.getCountryCode());
                                SharedHelper.putKey(EmailActivity.this, "mobile", phoneNumber.getPhoneNumber());
                                //register();
                            }
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.e("REQUEST_ACCOUNT_KIT", "onError: Account Kit" + accountKitError);
                        }
                    });
            }
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
