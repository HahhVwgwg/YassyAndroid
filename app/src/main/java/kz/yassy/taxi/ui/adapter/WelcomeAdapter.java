package kz.yassy.taxi.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import kz.yassy.taxi.ui.activity.WelcomeFragment;

public class WelcomeAdapter extends FragmentStateAdapter {

    public WelcomeAdapter(FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return WelcomeFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
