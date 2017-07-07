package com.adalbero.app.lebenindeutschland.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.adalbero.app.lebenindeutschland.ui.common.ResultCallback;
import com.adalbero.app.lebenindeutschland.ui.exam.ExamActivity;
import com.adalbero.app.lebenindeutschland.ui.settings.SettingsActivity;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ResultCallback {

    private FirebaseAnalytics mFirebaseAnalytics;

    private List<Exam> data;
    private ExamItemAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_main);

        init();

        updateData();

        mAdapter = new ExamItemAdapter(this, data);

        mListView = findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                onItemSelected(position);
            }
        });

        AppController.initAdView(this);

        Analytics.init(mFirebaseAnalytics);
    }

    private void init() {
        if (Store.getString(ExamSearch.KEY, null) == null) {
            List<String> terms = Arrays.asList(new String[] {"DDR"});
            Store.setList(ExamSearch.KEY, terms);
        }

        if (Store.getString(ExamTag.KEY, null) == null) {
            List<String> terms = Arrays.asList(new String[] {"image"});
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
            data = new ArrayList();
            for (Exam exam : AppController.getExamList()) {
                data.add(exam);
            }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                goSettings();
                return true;
            case R.id.menu_test:
                doTest();
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
        mFirebaseAnalytics.setCurrentScreen(this, "Main", null);
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

        Analytics.logExamView(mFirebaseAnalytics, exam);
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
            Analytics.logSearch(mFirebaseAnalytics, exam);

            goExam(name);
        } else if (parent instanceof WelcomeDialog) {
            String land = (String)param;

            Store.setLand(land);
            Analytics.logBundesland(mFirebaseAnalytics, land);

            updateData();
        }

    }

    private void doTest() {
        doSelectLand();
    }
}
