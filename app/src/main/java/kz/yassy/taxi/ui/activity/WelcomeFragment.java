package kz.yassy.taxi.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseFragment;

public class WelcomeFragment extends BaseFragment {

    @BindView(R.id.welcome_title)
    TextView title;
    @BindView(R.id.welcome_desc)
    TextView desc;
    @BindView(R.id.welcome_pic)
    ImageView pic;

    private static final String POS = "pos";

    public static WelcomeFragment newInstance(int pos) {
        final WelcomeFragment f = new WelcomeFragment();
        final Bundle b = new Bundle();
        b.putInt(POS, pos);
        f.setArguments(b);
        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.welcome_fragment;
    }

    @Override
    protected View initView(View view) {
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int position = requireArguments().getInt(POS);
        switch (position) {
            case 0:
                pic.setImageResource(R.drawable.ic_city_driver_cuate_1);
                title.setText(R.string.welcome_title_1);
                desc.setText(R.string.welcome_desc_1);
                break;
            case 1:
                pic.setImageResource(R.drawable.ic_city_girl_rafiki_1);
                title.setText(R.string.welcome_title_2);
                desc.setText(R.string.welcome_desc_2);
                break;
            default:
                pic.setImageResource(R.drawable.ic_city_driver_rafiki);
                title.setText(R.string.welcome_title_3);
                desc.setText(R.string.welcome_desc_3);
        }
    }
}
