package kz.yassy.taxi.ui.countrypicker;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kz.yassy.taxi.R;
import kz.yassy.taxi.base.BaseActivity;
import kz.yassy.taxi.data.SharedHelper;

public class CountryPicker extends BaseActivity {

    private CountryListAdapter adapter;
    private List<Country> countriesList = new ArrayList<>();
    private List<Country> selectedCountriesList = new ArrayList<>();
    private CountryPickerListener listener;

//    public static CountryPicker newInstance(String dialogTitle) {
//        CountryPicker picker = new CountryPicker();
//        Bundle bundle = new Bundle();
//        bundle.putString("dialogTitle", dialogTitle);
//        picker.setArguments(bundle);
//        return picker;
//    }


    @Override
    protected int getLayoutId() {
        return R.layout.country_picker;
    }


    @Override
    protected void initView() {
        setCountriesList(Country.getAllCountries());
        EditText searchEditText = findViewById(R.id.country_code_picker_search);
        ListView countryListView = findViewById(R.id.country_code_picker_listview);
        findViewById(R.id.back_btn).setOnClickListener(view -> finish());
        selectedCountriesList = new ArrayList<>(countriesList.size());
        selectedCountriesList.addAll(countriesList);

        adapter = new CountryListAdapter(this, selectedCountriesList);
        countryListView.setAdapter(adapter);
        countryListView.setDivider(null);

        countryListView.setOnItemClickListener((parent, view1, position, id) -> {
            Country country = selectedCountriesList.get(position);
            SharedHelper.putKey(getApplicationContext(), "countrySelected", country.getName());
            finish();
        });


        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });
    }

    public void setListener(CountryPickerListener listener) {
        this.listener = listener;
    }

    @SuppressLint("DefaultLocale")
    private void search(String text) {
        selectedCountriesList.clear();
        for (Country country : countriesList)
            if (country.getName().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase()))
                selectedCountriesList.add(country);
        adapter.notifyDataSetChanged();
    }

    public void setCountriesList(List<Country> newCountries) {
        this.countriesList.clear();
        this.countriesList.addAll(newCountries);
    }

}
