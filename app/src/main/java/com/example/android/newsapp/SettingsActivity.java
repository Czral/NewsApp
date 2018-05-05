package com.example.android.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference keyword = findPreference(getString(R.string.keyword_key));
            bindPreferenceToObject(keyword);

            Preference section = findPreference(getString(R.string.section_key));
            bindPreferenceToObject(section);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object object) {

            String search = object.toString();

            if (preference instanceof ListPreference) {

                ListPreference sectionList = (ListPreference) preference;
                int prefIndex = sectionList.findIndexOfValue(search);
                if (prefIndex > 0) {

                    CharSequence[] sections = sectionList.getEntries();
                    preference.setSummary(sections[prefIndex]);
                } else

                    preference.setSummary(search);
            } else {

                preference.setSummary(search);
            }

            return true;
        }

        private void bindPreferenceToObject(Preference preference) {

            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);

        }

    }

}
