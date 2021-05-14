package kz.yassy.taxi.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import kz.yassy.taxi.ui.fragment.service.ServiceTypeFragment;
import kz.yassy.taxi.ui.fragment.service.ServiceTypesFragment;

public class ServicePagerAdapter extends FragmentStateAdapter {

    private final ServiceTypesFragment.ServiceData serviceData;

    public ServicePagerAdapter(Fragment activity, ServiceTypesFragment.ServiceData serviceData) {
        super(activity);
        this.serviceData = serviceData;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ServiceTypeFragment.create(serviceData.getServices().get(position), serviceData.getPrices().get(position));
    }

    @Override
    public int getItemCount() {
        return serviceData.getServices().size();
    }
}