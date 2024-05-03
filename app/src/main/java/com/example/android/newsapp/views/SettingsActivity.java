package com.example.android.newsapp.views;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.android.newsapp.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

    }

    public static class NewsPreferenceFragment extends PreferenceFragmentCompat
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            addPreferencesFromResource(R.xml.settings_main);

            androidx.preference.Preference keyword = findPreference(getString(R.string.keyword_key));
            assert keyword != null;
            bindPreferenceToObject(keyword);

            androidx.preference.Preference section = findPreference(getString(R.string.section_key));
            assert section != null;
            bindPreferenceToObject(section);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object object) {

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
