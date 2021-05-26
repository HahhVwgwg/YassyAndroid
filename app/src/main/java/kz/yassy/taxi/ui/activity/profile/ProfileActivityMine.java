package kz.yassy.taxi.ui.activity.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.data.network.model.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static kz.yassy.taxi.data.SharedHelper.key.PROFILE_IMG;

public class ProfileActivityMine extends BaseActivity implements ProfileIView {

    public static final int REQUEST_PERMISSION = 100;
    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.back_btn)
    View back;
    @BindView(R.id.logout)
    TextView logout;
    @BindView(R.id.full_name)
    TextView fullName;
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.email)
    TextView email;
    File imgFile = null;
    private ProfilePresenter<ProfileActivityMine> profilePresenter = new ProfilePresenter<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_profile_mine;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        profilePresenter.attachView(this);
        Glide.with(baseActivity())
                .load(SharedHelper.getKey(baseActivity(), PROFILE_IMG))
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate()
                        .error(R.drawable.ic_user_placeholder))
                .into(picture);
        showLoading();
        profilePresenter.profile();
        SharedHelper.putKey(getApplicationContext(), "isRefreshNeed", false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SharedHelper.getKey(getApplicationContext(), "isRefreshNeed", false)) {
            showLoading();
            profilePresenter.profile();
        }
    }

    @OnClick({R.id.back_btn, R.id.logout, R.id.changeName, R.id.changeEmail, R.id.picture})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.picture:
                if (hasPermission(Manifest.permission.CAMERA) && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    pickImage();
                else
                    requestPermissionsSafely(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.logout:
                ShowLogoutPopUp();
                break;
            case R.id.changeName:
                if (fullName.getText().length() > 0) {
                    Intent intent = new Intent(this, ProfileChangePasswordActivity.class);
                    intent.putExtra("type", 1);
                    intent.putExtra("fullName", fullName.getText());
                    intent.putExtra("mobile", mobile.getText());
                    intent.putExtra("email", email.getText().toString().equals("Добавить почту") ? "yassy@gmail.com" : email.getText());
                    startActivity(intent);
                }
                break;
            case R.id.changeEmail:
                if (fullName.getText().length() > 0) {
                    Intent intent = new Intent(this, ProfileChangePasswordActivity.class);
                    intent.putExtra("type", 3);
                    intent.putExtra("fullName", fullName.getText());
                    intent.putExtra("mobile", mobile.getText());
                    intent.putExtra("email", email.getText().toString().equals("Добавить почту") ? "yassy@gmail.com" : email.getText());
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, ProfileActivityMine.this, new DefaultCallback() {
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
        updateDetails();
    }

    @SuppressLint("SetTextI18n")
    private void updateDetails() {
        HashMap<String, RequestBody> map = new HashMap<>();
//        map.put("email", RequestBody.create(MediaType.parse("text/plain"), email.getText().toString()));
        map.put("mobile", RequestBody.create(MediaType.parse("text/plain"), mobile.getText().toString()));
        map.put("country_code", RequestBody.create(MediaType.parse("text/plain"), "7"));
        map.put("gender", RequestBody.create(MediaType.parse("text/plain"), ("Male")));
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

    public void ShowLogoutPopUp() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivityMine.this);
        alertDialogBuilder
                .setMessage(getString(R.string.are_sure_you_want_to_logout)).setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> profilePresenter.logout(SharedHelper.getKey(this, "user_id")))
                .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(User user) {
        hideLoading();
        Log.e("user", user.toString());
        fullName.setText(user.getFirstName().equals("") ? "Ваше имя" : user.getFirstName() + " " + user.getLastName());
        mobile.setText(user.getMobile());
        if (user.getEmail().equals("yassy@gmail.com") || user.getMobile().equals(user.getEmail())) {
            email.setText("Добавить почту");
        } else {
            email.setText(user.getEmail());
        }
        Glide.with(baseActivity())
                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate().error(R.drawable.ic_user_placeholder))
                .into(picture);
    }

    @Override
    public void onUpdateSuccess(User user) {
        if (imgFile != null)
            Toasty.success(this, getText(R.string.profile_updated), Toast.LENGTH_SHORT).show();
        Glide.with(baseActivity())
                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate().error(R.drawable.ic_user_placeholder))
                .into(picture);
        hideLoading();
    }

    @Override
    public void onSuccessPhoneNumber(Object object) {

    }

    @Override
    public void onVerifyPhoneNumberError(Throwable e) {

    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        Log.e("userMine", e.toString());
    }
}
