package com.adalbero.app.lebenindeutschland;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Debug;
import com.adalbero.app.lebenindeutschland.controller.Statistics;

import java.util.List;

import static com.adalbero.app.lebenindeutschland.R.xml.preferences;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREF_LAND = "pref.land";
    public static final String PREF_VERSION = "pref.version";

    public static final String PREF_STAT_MAX = "pref.stat.max";
    public static final String PREF_REMOVE_STAT = "pref.remove.stat";

    public static final String BETA_CATEGORY = "beta.category";

    public static final String DEBUG_CATEGORY = "debug.category";
    public static final String DEBUG_DUMP = "debug.dump";
    public static final String DEBUG_DUMP_STAT = "debug.dump.stat";
    public static final String DEBUG_REMOVE_ALL = "debug.remove.all";
    public static final String DEBUG_REMOVE_EXAM = "debug.remove.exam";
    public static final String DEBUG_REMOVE_PREF = "debug.remove.pref";
    public static final String DEBUG_TEST = "debug.test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
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
        private PreferenceCategory mDebugCategory;
        private PreferenceCategory mBetaCategory;

        private int mDebugClick;
        private final int DEBUG_CLICKS = 3;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(preferences);

            initBundesland();
            initStatistics();

            Preference pref1 = findPreference(PREF_VERSION);
            pref1.setSummary(appVersion());
            pref1.setOnPreferenceClickListener(this);

            findPreference(PREF_REMOVE_STAT).setOnPreferenceClickListener(this);

            mDebugCategory = (PreferenceCategory) findPreference(DEBUG_CATEGORY);

            findPreference(DEBUG_DUMP).setOnPreferenceClickListener(this);
            findPreference(DEBUG_DUMP_STAT).setOnPreferenceClickListener(this);
            findPreference(DEBUG_REMOVE_ALL).setOnPreferenceClickListener(this);
            findPreference(DEBUG_REMOVE_EXAM).setOnPreferenceClickListener(this);
            findPreference(DEBUG_REMOVE_PREF).setOnPreferenceClickListener(this);
            findPreference(DEBUG_TEST).setOnPreferenceClickListener(this);

            mDebugClick = 0;

            mBetaCategory = (PreferenceCategory) findPreference(BETA_CATEGORY);

            getPreferenceScreen().removePreference(mDebugCategory);
            getPreferenceScreen().removePreference(mBetaCategory);
        }

        private void initBundesland() {
            ListPreference listPreference = (ListPreference) findPreference(PREF_LAND);
            List<String> list = AppController.getInstance().getQuestionDB().listDistinctLand();
            int n = list.size();

            String land_code[] = new String[n];
            String land_name[] = new String[n];

            for (int i = 0; i < n; i++) {
                String value = list.get(i);
                land_code[i] = value;
                land_name[i] = value.substring(3);
            }

            listPreference.setEntries(land_name);
            listPreference.setEntryValues(land_code);
        }

        private void initStatistics() {
            ListPreference listPreference = (ListPreference) findPreference(PREF_STAT_MAX);

            String values[] = new String[]{"1", "3", "5", "10"};
            String labels[] = new String[values.length];

            labels[0] = "Keep just last response";
            for (int i=1; i<values.length; i++) {
                labels[i] = String.format("Keep last %s responses", values[i]);
            }

            listPreference.setEntryValues(values);
            listPreference.setEntries(labels);
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
            String key = preference.getKey();
            if (key.equals(PREF_VERSION)) {
                if (mDebugClick == DEBUG_CLICKS - 1) {
                    Toast.makeText(getActivity(), "Enable Debug Mode", Toast.LENGTH_SHORT).show();
                    getPreferenceScreen().addPreference(mDebugCategory);
                    getPreferenceScreen().addPreference(mBetaCategory);
                } else {
                    mDebugClick++;
                }
            } else if (key.equals(DEBUG_DUMP)) {
                Debug.dumpSharedPreferences();
            } else if (key.equals(DEBUG_DUMP_STAT)) {
                Debug.dumpStatistics();
            } else if (key.equals(DEBUG_REMOVE_ALL)) {
                Debug.removeAll();
            } else if (key.equals(DEBUG_REMOVE_EXAM)) {
                Debug.removeExam();
            } else if (key.equals(DEBUG_REMOVE_PREF)) {
                Debug.removePref();
            } else if (key.equals(PREF_REMOVE_STAT)) {
                doRemoveStat();
            } else if (key.equals(DEBUG_TEST)) {
                Debug.doTest(100);
                Toast.makeText(getActivity(), "100 tests executed", Toast.LENGTH_SHORT).show();
            }

            return false;
        }

        private void doRemoveStat() {
            final Context context = getActivity();
            String title = "Remove Statistics";
            String text = "This will remove all statistics.\nDo you confirm?";
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(text)
                    .setTitle(title)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Debug.removeStat();
                            Statistics.getInstance().update();
                            Toast.makeText(context, "Statistics removed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null);
            builder.show();

        }

    }

}

