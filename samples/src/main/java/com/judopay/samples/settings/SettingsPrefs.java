package com.judopay.samples.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsPrefs {

    private static final String SHARED_PREFS_NAME = "Judo-SampleApp";
    private static final String AVS = "Avs";
    private static final String MAESTRO = "Maestro";
    private static final String AMEX = "Amex";
    private final SharedPreferences sharedPreferences;

    public SettingsPrefs(Context context) {
        this.sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setAvsEnabled(boolean enabled) {
        saveBoolean(AVS, enabled);
    }

    public void setMaestroEnabled(boolean enabled) {
        saveBoolean(MAESTRO, enabled);
    }

    public void setAmexEnabled(boolean enabled) {
        saveBoolean(AMEX, enabled);
    }

    public boolean isAvsEnabled() {
        return sharedPreferences.getBoolean(AVS, false);
    }

    public boolean isAmexEnabled() {
        return sharedPreferences.getBoolean(AMEX, true);
    }

    public boolean isMaestroEnabled() {
        return sharedPreferences.getBoolean(MAESTRO, false);
    }

    private void saveBoolean(String key, boolean enabled) {
        sharedPreferences.edit()
                .putBoolean(key, enabled)
                .apply();
    }

}
