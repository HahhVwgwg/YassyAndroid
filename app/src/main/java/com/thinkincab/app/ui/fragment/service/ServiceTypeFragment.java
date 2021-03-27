package com.thinkincab.app.ui.fragment.service;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseFragment;
import com.thinkincab.app.data.network.model.Service;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ServiceTypeFragment extends BaseFragment {

    @BindView(R.id.service_car)
    ImageView car;
    @BindView(R.id.service_title)
    TextView fareTitle;
    @BindView(R.id.service_desc)
    TextView fareDesc;
    @BindView(R.id.service_price)
    TextView farePrice;

    public static ServiceTypeFragment create(Service service, int price) {
        ServiceTypeFragment serviceTypeFragment = new ServiceTypeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", service);
        bundle.putInt("price", price);
        serviceTypeFragment.setArguments(bundle);
        return serviceTypeFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_service_type;
    }

    @Override
    protected View initView(View view) {
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            Service service = (Service) getArguments().getSerializable("data");
            int price = getArguments().getInt("price", 0);
            fareTitle.setText(service.getName());
            farePrice.setText(getString(R.string.sum_template, price));
            if (service.getId() == 1) {
                fareDesc.setText(R.string.fare_standard_desc);
                car.setImageResource(R.drawable.car_standard_big);
            } else if (service.getId() == 2) {
                fareDesc.setText(R.string.fare_comfort_desc);
                car.setImageResource(R.drawable.car_comfort_big);
            }
        }

    }
}
