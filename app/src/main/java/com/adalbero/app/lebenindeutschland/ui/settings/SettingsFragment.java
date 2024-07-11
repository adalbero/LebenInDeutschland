package com.adalbero.app.lebenindeutschland.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.widget.Toast;

import com.adalbero.app.lebenindeutschland.controller.Analytics;
import com.adalbero.app.lebenindeutschland.controller.Debug;
import com.adalbero.app.lebenindeutschland.controller.Statistics;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.UserMessagingPlatform;
import com.google.firebase.analytics.FirebaseAnalytics;

import static com.adalbero.app.lebenindeutschland.R.xml.preferences;
import static com.adalbero.app.lebenindeutschland.controller.Store.PREF_REMOVE_STAT;
import static com.adalbero.app.lebenindeutschland.controller.Store.PREF_VERSION;

/**
 * Created by Adalbero on 10/06/2017.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private FirebaseAnalytics mFirebaseAnalytics;
    private ConsentInformation consentInformation;

    public static final String PREF_DONATE = "pref.donate";
    public static final String PRIVACY_CATEGORY = "privacy.category";
    public static final String PREF_PRIVACY_POLICY = "pref.privacy.policy";
    public static final String PREF_PRIVACY_OPTIONS = "pref.privacy.options";
    public static final String DEBUG_CATEGORY = "debug.category";
    public static final String DEBUG_DUMP = "debug.dump";
    public static final String DEBUG_DUMP_STAT = "debug.dump.stat";
    public static final String DEBUG_REMOVE_ALL = "debug.remove.all";
    public static final String DEBUG_REMOVE_EXAM = "debug.remove.exam";
    public static final String DEBUG_REMOVE_PREF = "debug.remove.pref";
    public static final String DEBUG_TEST = "debug.test";

    private PreferenceCategory mDebugCategory;

    private int mDebugClick;
    private final int DEBUG_CLICKS = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        consentInformation = UserMessagingPlatform.getConsentInformation(getActivity());

        addPreferencesFromResource(preferences);

        initExcerciseSize();
        initStatistics();

        Preference pref1 = findPreference(PREF_VERSION);
        pref1.setSummary(appVersion());
        pref1.setOnPreferenceClickListener(this);

        Preference prefInline = findPreference(Store.PREF_INLINE_MODE);
        prefInline.setOnPreferenceChangeListener(this);

        Preference prefLand = findPreference(Store.PREF_LAND);
        prefLand.setOnPreferenceChangeListener(this);

        findPreference(PREF_REMOVE_STAT).setOnPreferenceClickListener(this);

        Preference prefDonate = findPreference(PREF_DONATE);
        prefDonate.setOnPreferenceClickListener(this);

        PreferenceCategory privacyCategory = (PreferenceCategory) findPreference(PRIVACY_CATEGORY);

        Preference prefPrivacyPolicy = findPreference(PREF_PRIVACY_POLICY);
        prefPrivacyPolicy.setOnPreferenceClickListener(this);

        Preference prefPrivacyOptions = findPreference(PREF_PRIVACY_OPTIONS);
        prefPrivacyOptions.setOnPreferenceClickListener(this);
        if (consentInformation.getPrivacyOptionsRequirementStatus() != ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED) {
            privacyCategory.removePreference(prefPrivacyOptions);
        }

        mDebugCategory = (PreferenceCategory) findPreference(DEBUG_CATEGORY);

        findPreference(DEBUG_DUMP).setOnPreferenceClickListener(this);
        findPreference(DEBUG_DUMP_STAT).setOnPreferenceClickListener(this);
        findPreference(DEBUG_REMOVE_ALL).setOnPreferenceClickListener(this);
        findPreference(DEBUG_REMOVE_EXAM).setOnPreferenceClickListener(this);
        findPreference(DEBUG_REMOVE_PREF).setOnPreferenceClickListener(this);
        findPreference(DEBUG_TEST).setOnPreferenceClickListener(this);

        mDebugClick = 0;

        getPreferenceScreen().removePreference(mDebugCategory);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initExcerciseSize() {
        ListPreference listPreference = (ListPreference) findPreference(Store.PREF_EXERCISE_SIZE);
        listPreference.setOnPreferenceChangeListener(this);

        String values[] = new String[]{"10", "20", "30", "50", "100"};
        String labels[] = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            labels[i] = String.format("%s questions", values[i]);
        }

        listPreference.setEntryValues(values);
        listPreference.setEntries(labels);
    }

    private void initStatistics() {
        ListPreference listPreference = (ListPreference) findPreference(Store.PREF_STAT_MAX);
        listPreference.setOnPreferenceChangeListener(this);

        String values[] = new String[]{"1", "3", "5", "10"};
        String labels[] = new String[values.length];

        labels[0] = "Keep just last answer";
        for (int i = 1; i < values.length; i++) {
            labels[i] = String.format("Keep last %s answers", values[i]);
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
                Analytics.logFeatureDebug(mFirebaseAnalytics, "enable");
            } else {
                mDebugClick++;
            }
        } else if (key.equals(PREF_DONATE)) {
            doOpenDonate();
        } else if (key.equals(PREF_PRIVACY_POLICY)) {
            doOpenPrivacyPolicy();
        } else if (key.equals(PREF_PRIVACY_OPTIONS)) {
            doOpenPrivacyOptions();
        } else if (key.equals(DEBUG_DUMP)) {
            Debug.dumpSharedPreferences();
        } else if (key.equals(DEBUG_DUMP_STAT)) {
            Debug.dumpStatistics();
        } else if (key.equals(DEBUG_REMOVE_ALL)) {
            doRemoveAll();
        } else if (key.equals(DEBUG_REMOVE_EXAM)) {
            Debug.removeExam();
        } else if (key.equals(DEBUG_REMOVE_PREF)) {
            Debug.removePref();
        } else if (key.equals(PREF_REMOVE_STAT)) {
            doRemoveStat();
        } else if (key.equals(DEBUG_TEST)) {
            Debug.doTest(25);
            Toast.makeText(getActivity(), "100 tests executed", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void doOpenDonate() {
        Analytics.logFeaturePolicy(mFirebaseAnalytics, "Donate");

        String url="https://ko-fi.com/appadalbero";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void doOpenPrivacyPolicy() {
        Analytics.logFeaturePolicy(mFirebaseAnalytics, "View Privacy Policy");

        String url="https://lidtest.de/policy.html#policy";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void doOpenPrivacyOptions() {
        Analytics.logFeaturePolicy(mFirebaseAnalytics, "Manage Policy Options");

        UserMessagingPlatform.showPrivacyOptionsForm(
                getActivity(),
                formError -> {
                    if (formError != null) {
                        Log.e("lid:SettingsFragment", String.format("doOpenPrivacyOptions: %s", formError));
                    }
                }
        );
    }

    private void doRemoveAll() {
        Analytics.logFeatureClearAll(mFirebaseAnalytics);
        consentInformation.reset();
        Debug.removeAll();
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

                        Analytics.logFeatureClearStat(mFirebaseAnalytics);
                    }
                })
                .setNegativeButton("No", null);
        builder.show();

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        String value = "" + obj;

        if (Store.PREF_LAND.equals(key)) {
            Preference prefLand = findPreference(Store.PREF_LAND);
            prefLand.setSummary("" + obj);
            Analytics.logFeatureLand(mFirebaseAnalytics, value, false);
        } else if (Store.PREF_STAT_MAX.equals(key)) {
            Analytics.logFeatureStatHistory(mFirebaseAnalytics, value);
        } else if (Store.PREF_INLINE_MODE.equals(key)) {
            Analytics.logFeatureInlineMode(mFirebaseAnalytics, value, null);
        }

        return true;
    }
}
