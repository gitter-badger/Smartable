package com.eagzzycsl.smartable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import common.Const;

/**
 * Created by eagzzycsl on 7/28/15.
 */
public class FragmentSetting extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Const {
    private EditTextPreference mEtPreference;
    private ListPreference mListPreference_1;
    private ListPreference mListPreference_2;
    private ListPreference mListPreference_3;
    private CheckBoxPreference mCheckPreference_1;
    private CheckBoxPreference mCheckPreference_2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_setting);
        initPreferences();
    }

    private void initPreferences() {
        mEtPreference = (EditTextPreference) findPreference(Const.EDIT_KEY);
        mListPreference_1 = (ListPreference) findPreference(Const.LANGUAGE);
        mListPreference_2 = (ListPreference) findPreference(Const.NIGHT_SUMMARY);
        mListPreference_3 = (ListPreference) findPreference(Const.WEEK_START);
        mCheckPreference_1 = (CheckBoxPreference) findPreference(Const.USL_LIGHT);
        mCheckPreference_2 = (CheckBoxPreference) findPreference(Const.DOUBLE_EXIT);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        mListPreference_1.setSummary(sharedPreferences.getString(Const.LANGUAGE, ""));
        mListPreference_2.setSummary(sharedPreferences.getString(Const.NIGHT_SUMMARY, ""));
        mListPreference_3.setSummary(sharedPreferences.getString(Const.WEEK_START, ""));
        mEtPreference.setSummary(sharedPreferences.getString(Const.EDIT_KEY, "linc"));

        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Const.LANGUAGE))
            mListPreference_1.setSummary(sharedPreferences.getString(key, ""));
        else if (key.equals(Const.NIGHT_SUMMARY))
            mListPreference_2.setSummary(sharedPreferences.getString(key, ""));
        else if (key.equals(Const.WEEK_START))
            mListPreference_3.setSummary(sharedPreferences.getString(key, ""));
        else if (key.equals(Const.EDIT_KEY))
            mEtPreference.setSummary(sharedPreferences.getString(key, "20"));
    }
}
