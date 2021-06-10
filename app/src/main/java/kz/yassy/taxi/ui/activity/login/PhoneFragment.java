package kz.yassy.taxi.ui.activity.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseFragment;
import kz.yassy.taxi.common.PlusSpan;
import kz.yassy.taxi.common.SpaceSpan;
import kz.yassy.taxi.data.SharedHelper;
import kz.yassy.taxi.ui.countrypicker.Country;
import kz.yassy.taxi.ui.countrypicker.CountryPicker;

@SuppressLint("NonConstantResourceId")
public class PhoneFragment extends BaseFragment {

    @BindView(R.id.auth_phone)
    EditText phone;
    @BindView(R.id.auth_phone_hint)
    TextView phoneHint;
    @BindView(R.id.next)
    MaterialButton next;
    @BindView(R.id.auth_root)
    ViewGroup root;
    @BindView(R.id.countryImage)
    ImageView countryImage;
    @BindView(R.id.countryNumber)
    TextView countryNumber;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_phone;
    }

    @Override
    protected View initView(View view) {
        ButterKnife.bind(this, view);
        next.setEnabled(false);
        setCountryList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserCountryInfo();
    }

    private void setCountryList() {
//        mCountryPicker = CountryPicker.newInstance("Select Country");
//        List<Country> countryList = Country.getAllCountries();
//        Collections.sort(countryList, (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
//        mCountryPicker.setCountriesList(countryList);

        setListener();
    }

    private void setListener() {
//        mCountryPicker.setListener((name, code, dialCode, flagDrawableResID) -> {
//            countryNumber.setText(dialCode);
//            countryImage.setImageResource(flagDrawableResID);
//            mCountryPicker.dismiss();
//        });
        assert getFragmentManager() != null;

        countryImage.setOnClickListener(v -> openActivity());

        countryNumber.setOnClickListener(v -> openActivity());

        getUserCountryInfo();
    }

    private void openActivity() {
        Intent intent = new Intent(getActivity(), CountryPicker.class);
        startActivity(intent);
    }

    private void getUserCountryInfo() {
        Country country = getDeviceCountry(getActivity());
        countryImage.setImageResource(country.getFlag());
        countryNumber.setText(country.getDialCode());
    }

    protected Country getDeviceCountry(Context context) {
        return Country.getCountryByName(SharedHelper.getKey(context, "countrySelected", "Kazakhstan"));
    }

    @OnClick(R.id.next)
    public void onClick(View view) {
        ((PhoneActivity) baseActivity()).onNext(phone.getText().toString());
    }

    @OnTextChanged(value = R.id.auth_phone, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onTextChanged(Editable s) {
        if (s.length() > 10) {
            phone.setText(s.toString().substring(0, 10));
            return;
        }
        next.setEnabled(s.length() == 10);
        if (s.length() == 10) {
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
//        String s1 = s.toString();
//        if (s1.length() > 0){
//            phone.setText(s1);
//            phone.setSelection(s1.length());
//            return;
//        }
//        System.out.println("Mine aldyngy "+ s1);
//        if (s1.startsWith("+7")) {
//            String v;
//            if (s1.startsWith("+78")) {
//                v = 7 + s1.substring(3, s.length());
//            } else {
//                v = 7 + s1.substring(2, s.length());
//            }
//            System.out.println("Mine " + v);
//            phone.setText(v);
//            phone.setSelection(v.length());
//            return;
//        } else if (s1.startsWith("7")) {
//            if (s1.startsWith("78")) {
//                String v = 7 + s1.substring(2, s.length());
//                System.out.println("Mine " + v );
//                phone.setText(v);
//                phone.setSelection(v.length());
//                return;
//            }
//        } else {
//            String v;
//            if (s1.startsWith("8")) {
//                v = 7 + s1.substring(1, s.length());
//            } else {
//                v = 7 + s1;
//            }
//            System.out.println("Mine " + v);
//
//        }
        if (4 <= length) {
            s.setSpan(new SpaceSpan(), 3, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (7 <= length) {
            s.setSpan(new SpaceSpan(), 6, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
//        System.out.println("MineString " + s1 );
        int end;
        switch (length) {
            case 0:
                end = 0;
                break;
            case 1:
                end = 1;
                break;
            case 2:
                end = 2;
                break;
            case 3:
                end = 4;
                break;
            case 4:
                end = 5;
                break;
            case 5:
                end = 6;
                break;
            case 6:
                end = 7;
                break;
            case 7:
                end = 9;
                break;
            case 8:
                end = 10;
                break;
            case 9:
                end = 11;
                break;
            default:
                end = 12;
        }
        phoneHint.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_grey));
        String fullHint = getString(R.string.auth_phone_hint);
        SpannableString result = new SpannableString(fullHint);
        result.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.transparent)), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        phoneHint.setText(result);
    }
}
