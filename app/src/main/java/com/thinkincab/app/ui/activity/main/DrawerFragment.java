package com.thinkincab.app.ui.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkincab.app.R;
import com.thinkincab.app.common.PlusSpan;
import com.thinkincab.app.common.SpaceSpan;
import com.thinkincab.app.data.network.model.MenuDrawer;
import com.thinkincab.app.data.network.model.User;
import com.thinkincab.app.ui.activity.profile.ProfileActivity;
import com.thinkincab.app.ui.adapter.DrawerAdapter;
import com.thinkincab.app.ui.utils.DisplayUtils;
import com.thinkincab.app.ui.utils.ListOffset;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DrawerFragment extends Fragment {

    @BindView(R.id.header)
    ViewGroup header;
    @BindView(R.id.header_phone)
    TextView headerPhone;
    @BindView(R.id.header_name)
    TextView headerName;
    @BindView(R.id.menu_list)
    RecyclerView menuList;
    @BindView(R.id.logout)
    View logout;

    private DrawerMenuListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DrawerMenuListener) {
            listener = (DrawerMenuListener) context;
        }
    }

    @Override
    public void onDetach() {
        listener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.left_menu_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        menuList.addItemDecoration(new ListOffset(DisplayUtils.dpToPx(14)));
        menuList.setAdapter(new DrawerAdapter(buildMenu(), item -> {
            if (listener != null) {
                listener.onMenuClick(item);
            }
        }));
    }

    @OnClick({R.id.header, R.id.logout})
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.header) {
            startActivity(new Intent(view.getContext(), ProfileActivity.class));
        } else if (viewId == R.id.logout) {
            if (listener != null) {
                listener.onMenuClick(new MenuDrawer.MenuLogout());
            }
        }
    }

    private List<MenuDrawer> buildMenu() {
        final List<MenuDrawer> result = new ArrayList<>();
        result.add(new MenuDrawer.MenuMain());
        result.add(new MenuDrawer.MenuFavorites());
        result.add(new MenuDrawer.MenuEarlyOrder());
        result.add(new MenuDrawer.MenuPayments());
        result.add(new MenuDrawer.MenuHistory());
        result.add(new MenuDrawer.MenuHelp());
        result.add(new MenuDrawer.MenuSettings());
        result.add(new MenuDrawer.MenuDriver());
        result.add(new MenuDrawer.MenuNews());
        result.add(new MenuDrawer.MenuInfo());
        return result;
    }

    public void updateUser(User user) {
        SpannableString ss = new SpannableString("7" + user.getEmail());
        try {
            ss.setSpan(new PlusSpan(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new SpaceSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new SpaceSpan(), 4, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new SpaceSpan(), 7, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        headerName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        headerPhone.setText(ss);
    }
}
