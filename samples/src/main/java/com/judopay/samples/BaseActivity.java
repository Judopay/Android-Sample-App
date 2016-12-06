package com.judopay.samples;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.judopay.model.Receipt;

abstract class BaseActivity extends AppCompatActivity {

    private static final int FINE_LOCATION_PERMISSION_REQUEST = 0x0009;

    private static final String TOKEN_RECEIPT_KEY = "Judo-SampleApp-TokenReceipt";

    public static final String SHARED_PREFS_NAME = "Judo-SampleApp";
    public static final String CURRENCY_KEY = "Judo-SampleApp-Currency";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST);
        }
    }

    void saveReceipt(Receipt receipt) {
        getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(TOKEN_RECEIPT_KEY, new Gson().toJson(receipt))
                .apply();
    }

    Receipt getLastReceipt() {
        String tokenReceiptJson = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
                .getString(TOKEN_RECEIPT_KEY, null);

        if (tokenReceiptJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(tokenReceiptJson, Receipt.class);
        }
        return null;
    }

}