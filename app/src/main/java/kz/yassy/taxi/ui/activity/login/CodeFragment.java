package kz.yassy.taxi.ui.activity.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseFragment;
import kz.yassy.taxi.common.SpaceSpan;
import kz.yassy.taxi.data.SharedHelper;

import static android.app.Activity.RESULT_OK;

@SuppressLint({"SetTextI18n", "DefaultLocale", "NonConstantResourceId"})
public class CodeFragment extends BaseFragment {

    @BindView(R.id.auth_code)
    EditText code;
    @BindView(R.id.auth_code_hint)
    TextView codeHint;
    @BindView(R.id.auth_error)
    TextView codeError;
    public final String TIMER_SECONDS = "TIMER_SECONDS";
    public final String BAN_COUNT = "BAN_COUNT";
    @BindView(R.id.next)
    MaterialButton next;
    @BindView(R.id.auth_root)
    ViewGroup root;

    private static final int REQ_USER_CONSENT = 200;
    SmsBroadcastReceiver smsBroadcastReceiver;
    @BindView(R.id.send_code_again)
    TextView sendCodeAgain;
    @BindView(R.id.auth_desc)
    TextView authDesc;
    private CountDownTimer timer;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_code;
    }

    @Override
    protected View initView(View view) {
        ButterKnife.bind(this, view);
        next.setEnabled(false);
        startSmartUserConsent();
        sendCodeAgain.setOnClickListener(view1 -> {
            if (sendCodeAgain.getText().equals("Отправить повторно")) {
                saveData();
                ((PhoneActivity) baseActivity()).onNextFromCodeFragment();
                resetTimer();
            }
        });

        return view;
    }

    private void saveData() {
        SharedHelper.putKey(getContext(), "BAN_TIME", System.currentTimeMillis());
        SharedHelper.putKey(getContext(), BAN_COUNT, SharedHelper.getKey(getContext(), BAN_COUNT, 0) + 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        resetTimer();
    }

    public void setWhatsAppSms() {
        authDesc.setText(getString(R.string.auth_code_desc_whatsapp));
    }

    private void resetTimer() {
        int secondsMilli = SharedHelper.getKey(getContext(), TIMER_SECONDS, 80000);
        if (timer != null)
            timer.cancel();
        if (SharedHelper.getKey(getContext(), BAN_COUNT, 0) >= 3) {
            System.out.println(SharedHelper.getKeyLong(getContext(), "BAN_TIME", 0L) + "time");
            secondsMilli = SharedHelper.getKeyLong(getContext(), "BAN_TIME", 0L) == 0L ? 2700000 : 2700000 - (int) (System.currentTimeMillis() - SharedHelper.getKeyLong(getContext(), "BAN_TIME", 0L));
            timer = new CountDownTimer(secondsMilli, 1000) {

                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000);
                    sendCodeAgain.setText("Отправить повторно через " + String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60) + " сек");
                    sendCodeAgain.setBackground(null);
                    SharedHelper.putKey(getContext(), TIMER_SECONDS, (int) (millisUntilFinished));
                }

                public void onFinish() {
                    sendCodeAgain.setText("Отправить повторно");
                    sendCodeAgain.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.card_chooser_btn_bbg));
                    SharedHelper.putKey(getContext(), TIMER_SECONDS, 2700000);
                }
            }.start();
        } else {
            timer = new CountDownTimer(secondsMilli, 1000) {

                @SuppressLint("SetTextI18n")
                public void onTick(long millisUntilFinished) {
                    sendCodeAgain.setText("Отправить повторно через " + millisUntilFinished / 1000 + " сек");
                    sendCodeAgain.setBackground(null);
                    SharedHelper.putKey(getContext(), TIMER_SECONDS, (int) (millisUntilFinished));
                }

                public void onFinish() {
                    sendCodeAgain.setText("Отправить повторно");
                    sendCodeAgain.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.card_chooser_btn_bbg));
                    SharedHelper.putKey(getContext(), TIMER_SECONDS, 80000);
                }
            }.start();
        }

    }

    private void startSmartUserConsent() {

        SmsRetrieverClient client = SmsRetriever.getClient(getContext());
        client.startSmsUserConsent(null);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_USER_CONSENT) {

            if ((resultCode == RESULT_OK) && (data != null)) {

                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);


            }


        }

    }

    private void getOtpFromMessage(String message) {

        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()) {

            code.setText(matcher.group(0));

        }


    }

    private void registerBroadcastReceiver() {

        smsBroadcastReceiver = new SmsBroadcastReceiver();

        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {

                startActivityForResult(intent, REQ_USER_CONSENT);

            }

            @Override
            public void onFailure() {

            }
        };

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        getActivity().registerReceiver(smsBroadcastReceiver, intentFilter);

    }

    @Override
    public void onStart() {
        super.onStart();
        registerBroadcastReceiver();

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(smsBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
    }

    @OnClick(R.id.next)
    public void onClick(View view) {
        ((PhoneActivity) baseActivity()).onComplete(code.getText().toString());
    }

    @OnTextChanged(value = R.id.auth_code, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(Editable s) {
        if (s.length() == 6) {
            next.setEnabled(true);
            ((PhoneActivity) baseActivity()).onComplete(code.getText().toString());
        }
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
        result.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.transparent)), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        codeHint.setText(result);
    }

    public void clearCode() {
        code.setText("");
    }

    public void onCodeError() {
        codeError.setVisibility(View.VISIBLE);
    }
}