package com.thinkincab.app.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.thinkincab.app.ui.activity.login.CodeFragment;
import com.thinkincab.app.ui.activity.login.PhoneFragment;

public class LoginAdapter extends FragmentStateAdapter {

    private PhoneFragment phoneFragment;
    private CodeFragment codeFragment;

    public LoginAdapter(FragmentActivity activity) {
        super(activity);
        phoneFragment = new PhoneFragment();
        codeFragment = new CodeFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return phoneFragment;
        } else {
            return codeFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public CodeFragment getCodeFragment() {
        return codeFragment;
    }

    public PhoneFragment getPhoneFragment() {
        return phoneFragment;
    }
}