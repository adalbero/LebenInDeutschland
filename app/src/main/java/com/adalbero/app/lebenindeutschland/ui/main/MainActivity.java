package com.adalbero.app.lebenindeutschland.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.Analytics;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Dialog;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.exam.Exam;
import com.adalbero.app.lebenindeutschland.data.exam.ExamHeader;
import com.adalbero.app.lebenindeutschland.data.exam.ExamLand;
import com.adalbero.app.lebenindeutschland.data.exam.ExamSearch;
import com.adalbero.app.lebenindeutschland.data.exam.ExamTag;
import com.adalbero.app.lebenindeutschland.ui.common.AppBaseActivity;
import com.adalbero.app.lebenindeutschland.ui.common.ResultCallback;
import com.adalbero.app.lebenindeutschland.ui.exam.ExamActivity;
import com.adalbero.app.lebenindeutschland.ui.settings.SettingsActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppBaseActivity implements ResultCallback {

    private static final String TAG = "lid:MainActivity";

    private FirebaseAnalytics mFirebaseAnalytics;
    private ConsentInformation consentInformation;
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private List<Exam> data;
    private ExamItemAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        consentInformation = UserMessagingPlatform.getConsentInformation(this);

        this.setContentView(R.layout.activity_main);

        requestConsent();

        init();

        updateData();

        mAdapter = new ExamItemAdapter(this, data);

        mListView = findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener((adapter, v, position, id) -> onItemSelected(position));
    }

    private void requestConsent() {

        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .build();

        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            this,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                    Log.w(TAG, String.format("ConsentFormDismissed: [%s] %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }

                                // Consent has been gathered.
                                if (consentInformation.canRequestAds()) {
                                    initializeMobileAdsSdk();
                                }
                            }
                    );
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w(TAG, String.format("ConsentInfoUpdateFailure: [%s] %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk();
        }

    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }


        Log.d(TAG, "initializeMobileAdsSdk - " + consentInformation.getConsentStatus());

        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this);
        AppController.initAdView(this);
    }

    private void init() {
        if (Store.getString(ExamSearch.KEY, null) == null) {
            List<String> terms = Collections.singletonList("DDR");
            Store.setList(ExamSearch.KEY, terms);
        }

        if (Store.getString(ExamTag.KEY, null) == null) {
            List<String> terms = Collections.singletonList("image");
            Store.setList(ExamTag.KEY, terms);
        }
    }

    private void onItemSelected(int position) {
        Exam exam = data.get(position);

        if (exam instanceof ExamHeader) return;

        AppController.setExamIdx(position);

        if (exam.onPrompt(this, this)) {
            return;
        } else if (exam instanceof ExamLand) {
            String land = Store.getSelectedLandCode();
            if (land == null) {
                doSelectLand();
                return;
            }
        }

        goExam(exam.getName());
    }

    private void updateData() {

        if (data == null) {
            data = new ArrayList<>();
            data.addAll(AppController.getExamList());
        } else {
            for (Exam exam : data) {
                exam.resetQuestionList();
            }

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                goSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mListView.setSelection(AppController.getExamIdx());

        Store.resetExamResult();

        if (Store.getLand() == null) {
            doSelectLand();
        }

        updateData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mFirebaseAnalytics.setCurrentScreen(this, "Main", null);
    }

    private void goSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void goExam(String examName) {
        Exam exam = AppController.getExam(examName);

        if (exam.getSize() == 0) {
            Dialog.promptDialog(this, "No questions in list " + exam.getTitle(false));
            updateData();
            return;
        }

        Store.setExamName(examName);
        Store.setQuestionIdx(0);

        Intent intent = new Intent(this, ExamActivity.class);
        this.startActivity(intent);
    }

    private void doSelectLand() {
        WelcomeDialog dialog = new WelcomeDialog();
        dialog.setCallback(this);
        dialog.show(this.getFragmentManager(), "land");
    }

    @Override
    public void onResult(Object parent, Object param) {

        if (parent instanceof Exam) {
            String name = (String) param;

            Exam exam = (Exam) parent;
            Analytics.logFeatureSearch(mFirebaseAnalytics, exam);

            goExam(name);
        } else if (parent instanceof WelcomeDialog) {
            String land = (String) param;

            Analytics.logFeatureLand(mFirebaseAnalytics, land, true);

            updateData();
        }

    }

}
