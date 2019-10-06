package com.judopay.samples.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.judopay.samples.MainActivity;
import com.judopay.samples.R;

import java.util.Arrays;
import java.util.List;

import static com.judopay.model.Currency.AUD;
import static com.judopay.model.Currency.BRL;
import static com.judopay.model.Currency.CAD;
import static com.judopay.model.Currency.CHF;
import static com.judopay.model.Currency.CZK;
import static com.judopay.model.Currency.DKK;
import static com.judopay.model.Currency.EUR;
import static com.judopay.model.Currency.GBP;
import static com.judopay.model.Currency.HKD;
import static com.judopay.model.Currency.HUF;
import static com.judopay.model.Currency.JPY;
import static com.judopay.model.Currency.NOK;
import static com.judopay.model.Currency.NZD;
import static com.judopay.model.Currency.PLN;
import static com.judopay.model.Currency.SEK;
import static com.judopay.model.Currency.SGD;
import static com.judopay.model.Currency.USD;
import static com.judopay.model.Currency.ZAR;

public class SettingsActivity extends AppCompatActivity {
    SwitchCompat avsSwitch;
    SwitchCompat maestroSwitch;
    SwitchCompat amexSwitch;
    Spinner currencySpinner;

    private SettingsPrefs prefs;
    private List<String> currencyCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        avsSwitch = findViewById(R.id.avs_switch);
        maestroSwitch = findViewById(R.id.maestro_switch);
        amexSwitch = findViewById(R.id.amex_switch);
        currencySpinner = findViewById(R.id.currency_spinner);

        currencyCodes = Arrays.asList(AUD, BRL, CAD, CHF, CZK, DKK, EUR, GBP, HKD, HUF, JPY, NOK, NZD, PLN, SEK, SGD, USD, ZAR);

        setTitle(R.string.settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.prefs = new SettingsPrefs(this);

        initialize();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencyCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        currencySpinner.setAdapter(adapter);
        currencySpinner.setSelection(getCurrencySelection());

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currency = currencyCodes.get(position);
                saveCurrency(currency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        avsSwitch.setChecked(prefs.isAvsEnabled());
        avsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.setAvsEnabled(isChecked));

        maestroSwitch.setChecked(prefs.isMaestroEnabled());
        maestroSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.setMaestroEnabled(isChecked));

        amexSwitch.setChecked(prefs.isAmexEnabled());
        amexSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.setAmexEnabled(isChecked));
    }

    private void saveCurrency(String currency) {
        getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(MainActivity.CURRENCY_KEY, currency)
                .apply();
    }

    private int getCurrencySelection() {
        String currency = getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MODE_PRIVATE)
                .getString(MainActivity.CURRENCY_KEY, GBP);

        return currencyCodes.indexOf(currency);
    }
}
