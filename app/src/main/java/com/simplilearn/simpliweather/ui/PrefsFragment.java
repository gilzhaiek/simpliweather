package com.simplilearn.simpliweather.ui;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.simplilearn.simpliweather.R;

public class PrefsFragment extends PreferenceFragmentCompat {
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
}