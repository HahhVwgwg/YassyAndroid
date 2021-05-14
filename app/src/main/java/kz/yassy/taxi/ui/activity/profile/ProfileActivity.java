package kz.yassy.taxi.ui.activity.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import kz.yassy.taxi.BuildConfig;
import kz.yassy.taxi.MvpApplication;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.model.User;
import kz.yassy.taxi.ui.activity.change_password.ChangePasswordActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static kz.yassy.taxi.data.SharedHelper.key.PROFILE_IMG;

public class ProfileActivity extends BaseActivity implements ProfileIView {

    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.completeData)
    LinearLayout completeData;
    @BindView(R.id.first_name)
    EditText firstName;
    @BindView(R.id.last_name)
    EditText lastName;
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.email)
    EditText email;
    File imgFile = null;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.change_password)
    TextView changePassword;
    @BindView(R.id.qr_scan)
    ImageView ivQrScan;

    public static final int REQUEST_PERMISSION = 100;
    private ProfilePresenter<ProfileActivity> profilePresenter = new ProfilePresenter<>();
    private String qrCodeUrl;
    private AlertDialog mDialog;
    private String userAvatar;
    @BindView(R.id.sexy_spinner)
    AppCompatSpinner sexy_Spinner;
    @Override
    public int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        profilePresenter.attachView(this);
        setTitle(getString(R.string.profile));

        showLoading();
        profilePresenter.profile();
        Glide.with(baseActivity())
                .load(SharedHelper.getKey(baseActivity(), PROFILE_IMG))
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate()
                        .error(R.drawable.ic_user_placeholder))
                .into(picture);

        setupSpinnerCities();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, ProfileActivity.this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                imgFile = imageFiles.get(0);
                Glide.with(baseActivity())
                        .load(Uri.fromFile(imgFile))
                        .apply(RequestOptions
                                .placeholderOf(R.drawable.ic_user_placeholder)
                                .dontAnimate()
                                .error(R.drawable.ic_user_placeholder))
                        .into(picture);
            }
        });
    }

    @Override
    public void onSuccessPhoneNumber(Object object) {
        updateDetails();
    }

    @Override
    public void onVerifyPhoneNumberError(Throwable e) {
        Toasty.error(this, getString(R.string.phone_number_already_exists), Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.picture, R.id.save, R.id.change_password, R.id.qr_scan})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.picture:
                if (hasPermission(Manifest.permission.CAMERA) && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    pickImage();
                else
                    requestPermissionsSafely(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                break;
            case R.id.save:
                updateProfile();
                break;
            case R.id.change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.qr_scan:
                if (!TextUtils.isEmpty(qrCodeUrl)) showQRCodePopup();
                break;
        }
    }

    private void showQRCodePopup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_qrcode_image, null);

        ImageView qrImage = view.findViewById(R.id.qr_image);
        ImageView close = view.findViewById(R.id.ivClose);

        Glide.with(ProfileActivity.this)
                .load(qrCodeUrl)
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ic_qr_code)
                        .dontAnimate().error(R.drawable.ic_qr_code))
                .into(qrImage);

        builder.setView(view);
        mDialog = builder.create();
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        close.setOnClickListener(view1 -> mDialog.dismiss());

        mDialog.show();
    }

    private void updateProfile() {
        if (userAvatar == null && imgFile == null) {
            Toast.makeText(this, "\n" + "Please submit your profile picture", Toast.LENGTH_SHORT).show();
        } else if (email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
        } else if (firstName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_first_name), Toast.LENGTH_SHORT).show();
        } else if (lastName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_last_name), Toast.LENGTH_SHORT).show();
        } else if (mobile.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
        } else updateDetails();
    }

    private void setupSpinnerCities(){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.gender_list, R.layout.spinner);
        adapter.setDropDownViewResource(R.layout.spinner);
        sexy_Spinner.setAdapter(adapter);
    }
    private void updateDetails() {
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("first_name", RequestBody.create(MediaType.parse("text/plain"), firstName.getText().toString()));
        map.put("last_name", RequestBody.create(MediaType.parse("text/plain"), lastName.getText().toString()));
        map.put("email", RequestBody.create(MediaType.parse("text/plain"), email.getText().toString()));
        map.put("mobile", RequestBody.create(MediaType.parse("text/plain"), mobile.getText().toString()));
        map.put("country_code", RequestBody.create(MediaType.parse("text/plain"), SharedHelper.getKey(ProfileActivity.this, "country_code")));
        map.put("gender",RequestBody.create(MediaType.parse("text/plain"),(sexy_Spinner.getSelectedItem().toString())));
        MultipartBody.Part filePart = null;
        if (imgFile != null)
            try {
                File compressedImageFile = new Compressor(this).compressToFile(imgFile);
                filePart = MultipartBody.Part.createFormData("picture", compressedImageFile.getName(),
                        RequestBody.create(MediaType.parse("image*//*"), compressedImageFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        showLoading();
        profilePresenter.update(map, filePart);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0) {
                    boolean permission1 = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean permission2 = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (permission1 && permission2) pickImage();
                    else
                        Toast.makeText(getApplicationContext(), R.string.please_give_permissions, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onSuccess(@NonNull User user) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        //Seta avatar
        userAvatar = user.getPicture();

        String loginBy = user.getLoginBy();
        SharedHelper.putKey(this, "lang", user.getLanguage());
        changePassword.setVisibility(loginBy.equalsIgnoreCase("facebook")
                || loginBy.equalsIgnoreCase("google") ? View.INVISIBLE : View.GONE);
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        mobile.setText(user.getMobile());
        email.setText(user.getEmail());
        SharedHelper.putKey(this, "stripe_publishable_key", user.getStripePublishableKey());
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        Glide.with(baseActivity())
                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate().error(R.drawable.ic_user_placeholder))
                .into(picture);
        MvpApplication.showOTP = user.getRide_otp().equals("1");

        qrCodeUrl = !TextUtils.isEmpty(user.getQrcode_url()) ? BuildConfig.BASE_URL + user.getQrcode_url() : null;
        ivQrScan.setVisibility(TextUtils.isEmpty(qrCodeUrl) ? View.GONE : View.GONE);
    }

    @Override
    public void onUpdateSuccess(User user) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        SharedHelper.putKey(this, "lang", user.getLanguage());

        SharedHelper.putKey(this, "stripe_publishable_key", user.getStripePublishableKey());
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        try {
            MvpApplication.showOTP = user.getRide_otp().equals("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
        Toasty.success(this, getText(R.string.profile_updated), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        profilePresenter.onDetach();
        super.onDestroy();
    }
}
