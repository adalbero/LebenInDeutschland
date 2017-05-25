package com.adalbero.app.lebenindeutschland;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.adalbero.app.lebenindeutschland.controller.AppController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREF_KEY_LAND = "pref_key_land";

    private AppController mAppControler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");

        mAppControler = AppController.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

        getFragmentManager().beginTransaction().replace(R.id.fragment, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            initLand();

            Preference pref1 = findPreference("version");
            pref1.setSummary(appVersion());
            pref1.setOnPreferenceClickListener(this);
        }

        private void initLand() {
            ListPreference listPreference = (ListPreference) findPreference(PREF_KEY_LAND);
            List<String> list = AppController.getInstance().getQuestionDB().listDistinctLand();
            int n = list.size();

            String land_code[] = new String[n];
            String land_name[] = new String[n];

            for (int i=0; i<n; i++) {
                String value = list.get(i);
                land_code[i] = value;
                land_name[i] = value.substring(3);
            }

            listPreference.setEntries(land_name);
            listPreference.setEntryValues(land_code);
        }

        public String appVersion() {
            try {
                PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                return pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                return "unknown";
            }
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals("version") ) {
                Log.d("MyApp", "SettingsFragment.onPreferenceClick: ");
                dumpSharedPreferences();
            }
            return false;
        }

        public void dumpSharedPreferences() {
            Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(
                    AppController.getInstance()).getAll();
            Set<String> keys = new TreeSet<>(prefs.keySet());
            for (String key : keys) {
                Object value = prefs.get(key);
                Log.d("MyApp", "SettingsActivity.dump: " + key + " : " + value);
            }
        }

    }

}

