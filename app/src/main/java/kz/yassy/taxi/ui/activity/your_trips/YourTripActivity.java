package kz.yassy.taxi.ui.activity.your_trips;

import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.ui.fragment.past_trip.PastTripFragment;
import kz.yassy.taxi.ui.fragment.upcoming_trip.UpcomingTripFragment;

public class YourTripActivity extends BaseActivity {

    @BindView(R.id.back_btn)
    View back;
    //    @BindView(R.id.tabs)
//    TabLayout tabs;
    @BindView(R.id.container)
    FrameLayout container;

    TabPagerAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_your_trip;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        // Activity title will be updated after the locale has changed in Runtime
        if (getIntent().getExtras().getInt("type") == 1) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PastTripFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new UpcomingTripFragment()).commit();
        }
//        tabs.addTab(tabs.newTab().setText(getString(R.string.past)));
//        tabs.addTab(tabs.newTab().setText(getString(R.string.upcoming)));
//
//        adapter = new TabPagerAdapter(getSupportFragmentManager(), tabs.getTabCount());
//        container.setAdapter(adapter);
//        container.canScrollHorizontally(0);
//        container.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
//        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                container.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

    }

    @OnClick(R.id.back_btn)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    public class TabPagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        TabPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new PastTripFragment();
                case 1:
                    return new UpcomingTripFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
