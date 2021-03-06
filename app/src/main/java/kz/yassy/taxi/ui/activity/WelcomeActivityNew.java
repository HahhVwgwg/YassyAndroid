package kz.yassy.taxi.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.yassy.taxi.R;
import kz.yassy.taxi.data.network.model.CheckVersion;
import kz.yassy.taxi.data.network.model.Service;
import kz.yassy.taxi.data.network.model.User;
import kz.yassy.taxi.ui.activity.login.PhoneActivity;
import kz.yassy.taxi.ui.activity.splash.SplashIView;
import kz.yassy.taxi.ui.activity.splash.SplashPresenter;
import kz.yassy.taxi.ui.adapter.WelcomeAdapter;
import kz.yassy.taxi.ui.fragment.rate.ShowUpdateDialogFragment;

public class WelcomeActivityNew extends AppCompatActivity implements SplashIView {

    @BindView(R.id.next)
    MaterialButton next;
    @BindView(R.id.slider)
    ViewPager2 slider;
    @BindView(R.id.dots)
    TabLayout dots;

    private static void onConfigureTab(TabLayout.Tab tab, int p) {
        tab.view.setClickable(false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_welcomeactivity);
        ButterKnife.bind(this);
        slider.setAdapter(new WelcomeAdapter(this));
        new TabLayoutMediator(dots, slider, WelcomeActivityNew::onConfigureTab).attach();
        next.setCheckable(true);
        presenter.attachView(this);
        next.setOnClickListener(v -> {
            if (slider.getCurrentItem() < 2) {
                slider.setCurrentItem(slider.getCurrentItem() + 1);
            } else {
                next.setChecked(true);
                Intent nextScreen = new Intent(WelcomeActivityNew.this, PhoneActivity.class);
                nextScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nextScreen);
                finishAffinity();
            }
        });
        slider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                next.setChecked(position == 2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    private SplashPresenter<WelcomeActivityNew> presenter = new SplashPresenter<>();


    @Override
    public void onSuccess(List<Service> serviceList) {

    }

    @Override
    public void onSuccess(User user) {

    }

    @Override
    public Activity baseActivity() {
        return null;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() throws Exception {

    }

    @Override
    public void onSuccessLogout(Object object) {

    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(CheckVersion checkVersion) {
        if (checkVersion.getForceUpdate()) {
            new ShowUpdateDialogFragment(checkVersion.getUrl()).show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onLanguageChanged(Object object) {

    }
}
