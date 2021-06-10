package kz.yassy.taxi.ui.fragment.service;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseBottomSheetDialogFragment;
import kz.yassy.taxi.data.network.model.Service;
import kz.yassy.taxi.ui.adapter.ServicePagerAdapter;

import static kz.yassy.taxi.MvpApplication.RIDE_REQUEST;
import static kz.yassy.taxi.common.Constants.BroadcastReceiver.INTENT_FILTER;
import static kz.yassy.taxi.common.Constants.RIDE_REQUEST.SERVICE_TYPE;

public class ServiceTypesFragment extends BaseBottomSheetDialogFragment implements ServiceTypesIView {

    @BindView(R.id.slider)
    ViewPager2 slider;
    @BindView(R.id.service_dots)
    TabLayout dots;

    Unbinder unbinder;
    private ServicePagerAdapter adapter;

    public static ServiceTypesFragment create(List<Service> services, List<Integer> prices, int selected) {
        ServiceTypesFragment serviceTypesFragment = new ServiceTypesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", new ServiceData(services, selected, prices));
        serviceTypesFragment.setArguments(bundle);
        return serviceTypesFragment;
    }

    private final ServiceTypesPresenter<ServiceTypesFragment> presenter = new ServiceTypesPresenter<>();

    private ServiceData serviceData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            serviceData = (ServiceData) getArguments().getSerializable("data");
            adapter = new ServicePagerAdapter(this, serviceData);
            slider.setAdapter(adapter);
            slider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    RIDE_REQUEST.put(SERVICE_TYPE, serviceData.services.get(position).getId());
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                }
            });
            new TabLayoutMediator(dots, slider, (tab, position) -> {
                //Some implementation
            }).attach();
            slider.setCurrentItem(serviceData.getSelected(), false);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service;
    }

    @Override
    public void initView(View view) {
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
    }

    @OnClick({R.id.ride_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ride_now:
                dismiss();
//                sendRequest();
                break;
            default:
                break;
        }
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        handleError(e);
    }

    private void sendRequest() {
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        showLoading();
        presenter.rideNow(map);
    }

    @Override
    public void onSuccess(@NonNull Object object) {
        hideLoading();
        baseActivity().sendBroadcast(new Intent(INTENT_FILTER));
        dismiss();
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    public interface ServiceListener {
        void whenClicked(int pos);
    }

    public static class ServiceData implements Serializable {

        private final List<Service> services;
        private final List<Integer> prices;

        private final int selected;

        public ServiceData(List<Service> services, int selected, List<Integer> prices) {
            this.services = services;
            this.selected = selected;
            this.prices = prices;
        }

        public int getSelected() {
            return selected;
        }

        public List<Integer> getPrices() {
            return prices;
        }

        public List<Service> getServices() {
            return services;
        }
    }

}
