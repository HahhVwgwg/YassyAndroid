package com.thinkincab.app.ui.activity.login;

import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.thinkincab.app.R;
import com.thinkincab.app.base.BaseFragment;
import com.thinkincab.app.common.PlusSpan;
import com.thinkincab.app.common.SpaceSpan;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class PhoneFragment extends BaseFragment {

    @BindView(R.id.auth_phone)
    EditText phone;
    @BindView(R.id.auth_phone_hint)
    TextView phoneHint;
    @BindView(R.id.next)
    MaterialButton next;
    @BindView(R.id.auth_root)
    ViewGroup root;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_phone;
    }

    @Override
    protected View initView(View view) {
        ButterKnife.bind(this, view);
        next.setEnabled(false);
        return view;
    }

    @OnClick(R.id.next)
    public void onClick(View view) {
        ((PhoneActivity) baseActivity()).onNext(phone.getText().toString());
    }

    @OnTextChanged(value = R.id.auth_phone, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(Editable s) {
        next.setEnabled(s.length() == 11);
        if (s.length() == 11) {
            hideKeyboard(phone);
            root.requestFocus();
        }
        SpaceSpan[] spaceSpans = s.getSpans(0, s.length(), SpaceSpan.class);
        PlusSpan[] plusSpans = s.getSpans(0, s.length(), PlusSpan.class);
        for (SpaceSpan span : spaceSpans) {
            s.removeSpan(span);
        }
        for (PlusSpan span : plusSpans) {
            s.removeSpan(span);
        }
        int length = s.length();
        String s1 = s.toString();
        if (s1.startsWith("+7")) {
            String v;
            if (s1.startsWith("+78")) {
                v = 7 + s1.substring(3, s.length());
            } else {
                v = 7 + s1.substring(2, s.length());
            }
            phone.setText(v);
            phone.setSelection(v.length());
            return;
        } else if (s1.startsWith("7")) {
            if (s1.startsWith("78")) {
                String v = 7 + s1.substring(2, s.length());
                phone.setText(v);
                phone.setSelection(v.length());
                return;
            }
            s.setSpan(new PlusSpan(), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            String v;
            if (s1.startsWith("8")) {
                v = 7 + s1.substring(1, s.length());
            } else {
                v = 7 + s1;
            }
            phone.setText(v);
            phone.setSelection(v.length());
            return;
        }
        if (2 <= length) {
            s.setSpan(new SpaceSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (5 <= length) {
            s.setSpan(new SpaceSpan(), 4, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (8 <= length) {
            s.setSpan(new SpaceSpan(), 7, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        int end;
        switch (length) {
            case 0:
                end = 0;
                break;
            case 1:
                end = 2;
                break;
            case 2:
                end = 4;
                break;
            case 3:
                end = 5;
                break;
            case 4:
                end = 6;
                break;
            case 5:
                end = 8;
                break;
            case 6:
                end = 9;
                break;
            case 7:
                end = 10;
                break;
            case 8:
                end = 12;
                break;
            case 9:
                end = 13;
                break;
            case 10:
                end = 14;
                break;
            default:
                end = 15;
        }
        phoneHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_grey));
        String fullHint = getString(R.string.auth_phone_hint);
        SpannableString result = new SpannableString(fullHint);
        result.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.white)), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        phoneHint.setText(result);
    }
}
