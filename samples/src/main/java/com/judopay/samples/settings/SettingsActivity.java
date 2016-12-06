package com.judopay.samples.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.judopay.model.Currency;
import com.judopay.samples.MainActivity;
import com.judopay.samples.R;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat avsSwitch;
    SwitchCompat maestroSwitch;
    SwitchCompat amexSwitch;
    Spinner currencySpinner;

    private SettingsPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        avsSwitch = (SwitchCompat) findViewById(R.id.avs_switch);
        maestroSwitch = (SwitchCompat) findViewById(R.id.maestro_switch);
        amexSwitch = (SwitchCompat) findViewById(R.id.amex_switch);
        currencySpinner = (Spinner) findViewById(R.id.currency_spinner);

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
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initialize() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Currency.currencyNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        currencySpinner.setAdapter(adapter);
        currencySpinner.setSelection(getCurrencySelection());

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currency = Currency.currencyCodes().get(position);
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
                .getString(MainActivity.CURRENCY_KEY, Currency.GBP);

        return Currency.currencyCodes().indexOf(currency);
    }
}
