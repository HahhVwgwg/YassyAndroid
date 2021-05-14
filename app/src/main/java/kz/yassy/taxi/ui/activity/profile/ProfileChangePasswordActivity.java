package kz.yassy.taxi.ui.activity.profile;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.model.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileChangePasswordActivity extends BaseActivity implements ProfileIView {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    @BindView(R.id.back_btn)
    View back;
    @BindView(R.id.name)
    EditText editText;
    @BindView(R.id.submit)
    Button button;
    private ProfilePresenter<ProfileChangePasswordActivity> profilePresenter = new ProfilePresenter<>();
    private int type;

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_change_name;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        profilePresenter.attachView(this);
        type = getIntent().getExtras().getInt("type");
        if (type == 3) {
            editText.setHint("Введите свою почту");
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                button.setEnabled(editable.length() > 0);
            }
        });
    }

    @OnClick({R.id.back_btn, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.submit:
                if (type == 3) {
                    if (!validate(editText.getText().toString())) {
                        Toast.makeText(getApplicationContext(), "Недопустимый адрес электронной почты", Toast.LENGTH_SHORT).show();
                    } else {
                        updateDetails();
                    }
                } else {
                    updateDetails();
                }
                break;
        }
    }

    private void updateDetails() {
        HashMap<String, RequestBody> map = new HashMap<>();
        if (type == 1) {
            map.put("first_name", RequestBody.create(MediaType.parse("text/plain"), editText.getText().toString().split(" ")[0]));
            map.put("last_name", RequestBody.create(MediaType.parse("text/plain"), editText.getText().toString().split(" ").length > 1 ? editText.getText().toString().split(" ")[1] : ""));
            map.put("email", RequestBody.create(MediaType.parse("text/plain"), getIntent().getExtras().getString("email")));
        } else {
            map.put("first_name", RequestBody.create(MediaType.parse("text/plain"), getIntent().getExtras().getString("fullName").split(" ")[0]));
            map.put("last_name", RequestBody.create(MediaType.parse("text/plain"), getIntent().getExtras().getString("fullName").split(" ").length > 1 ? getIntent().getExtras().getString("fullName").split(" ")[1] : ""));
            map.put("email", RequestBody.create(MediaType.parse("text/plain"), editText.getText().toString()));
        }
        map.put("mobile", RequestBody.create(MediaType.parse("text/plain"), getIntent().getExtras().getString("mobile")));
        map.put("country_code", RequestBody.create(MediaType.parse("text/plain"), SharedHelper.getKey(getApplicationContext(), "country_code")));
        map.put("gender", RequestBody.create(MediaType.parse("text/plain"), ("Male")));
        MultipartBody.Part filePart = null;
//        if (imgFile != null)
//            try {
//                File compressedImageFile = new Compressor(this).compressToFile(imgFile);
//                filePart = MultipartBody.Part.createFormData("picture", compressedImageFile.getName(),
//                        RequestBody.create(MediaType.parse("image*//*"), compressedImageFile));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        showLoading();
        profilePresenter.update(map, filePart);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(User user) {
    }

    @Override
    public void onUpdateSuccess(User user) {
        hideLoading();
        Log.e("ErrorMine", "success");
        finish();
    }

    @Override
    public void onSuccessPhoneNumber(Object object) {

    }

    @Override
    public void onError(Throwable e) {
        Log.e("ErrorMine", e.getMessage());
        hideLoading();
        finish();
    }

    @Override
    public void onVerifyPhoneNumberError(Throwable e) {

    }
}
