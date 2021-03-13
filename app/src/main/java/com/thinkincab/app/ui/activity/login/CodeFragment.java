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
import com.thinkincab.app.common.SpaceSpan;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class CodeFragment extends BaseFragment {

    @BindView(R.id.auth_code)
    EditText code;
    @BindView(R.id.auth_code_hint)
    TextView codeHint;
    @BindView(R.id.auth_error)
    TextView codeError;
    @BindView(R.id.next)
    MaterialButton next;
    @BindView(R.id.auth_root)
    ViewGroup root;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_code;
    }

    @Override
    protected View initView(View view) {
        ButterKnife.bind(this, view);
        next.setEnabled(false);
        return view;
    }

    @OnClick(R.id.next)
    public void onClick(View view) {
        ((PhoneActivity) baseActivity()).onComplete(code.getText().toString());
    }

    @OnTextChanged(value = R.id.auth_code, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(Editable s) {
        next.setEnabled(s.length() == 6);
        codeError.setVisibility(View.GONE);
        if (s.length() == 6) {
            hideKeyboard(code);
            root.requestFocus();
        }
        SpaceSpan[] spaceSpans = s.getSpans(0, s.length(), SpaceSpan.class);
        for (SpaceSpan span : spaceSpans) {
            s.removeSpan(span);
        }
        int length = s.length();
        if (2 <= length) {
            s.setSpan(new SpaceSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (3 <= length) {
            s.setSpan(new SpaceSpan(), 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (4 <= length) {
            s.setSpan(new SpaceSpan(), 3, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (5 <= length) {
            s.setSpan(new SpaceSpan(), 4, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (6 <= length) {
            s.setSpan(new SpaceSpan(), 5, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                end = 6;
                break;
            case 4:
                end = 8;
                break;
            case 5:
                end = 10;
                break;
            default:
                end = 11;
        }
        codeHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_grey));
        String fullHint = getString(R.string.auth_code_hint);
        SpannableString result = new SpannableString(fullHint);
        result.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.white)), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        codeHint.setText(result);
    }

    public void clearCode() {
        code.setText("");
    }

    public void onCodeError() {
        codeError.setVisibility(View.VISIBLE);
    }
}