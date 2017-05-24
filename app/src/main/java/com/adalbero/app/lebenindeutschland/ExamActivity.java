package com.adalbero.app.lebenindeutschland;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.data.Exam;
import com.adalbero.app.lebenindeutschland.data.Question;

import java.util.ArrayList;
import java.util.List;

public class ExamActivity extends AppCompatActivity implements ResultCallback {

    private List<Question> data = new ArrayList();
    private Exam mExam;
    private ListView mListView;
    private QuestionItemAdapter mAdapter;

    private AppController mAppController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        Log.d("MyApp", "ExamActivity.onCreate: ");

        mAppController = (AppController) getApplication();

        String listName = mAppController.getString("exam_name", "???");
        mExam = mAppController.getExam(listName);

        List<String> questions = mExam.getQuestionNumList();

        String title = mExam.getTitle();
        getSupportActionBar().setTitle(title);

        if (questions != null) {
            for (String questionNum : questions) {
                Question q = mAppController.getQuestionDB().findByNum(questionNum);
                data.add(q);
            }
        }

        mAdapter = new QuestionItemAdapter(this, data, mExam, this);

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                boolean inline = (AppController.getInstance().getInt("exam.inline", 0) == 1);
                if (!inline)
                    goQuestion(position);
            }
        });

        mAppController.initAdView(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exam_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean inline = (AppController.getInstance().getInt("exam.inline", 0) == 1);

        menu.findItem(R.id.menu_expand).setVisible(!inline);
        menu.findItem(R.id.menu_collapse).setVisible(inline);

        return super.onPrepareOptionsMenu(menu);
    }

    private void setInline(boolean inline) {
        mAppController.putInt("exam.inline", inline ? 1 : 0);

        String msg = (inline ? "Question inline mode" : "Question view mode");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        invalidateOptionsMenu();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_expand:
                setInline(true);
                return true;
            case R.id.menu_collapse:
                setInline(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MyApp", "ExamActivity.onResume: ");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("MyApp", "ExamActivity.onStart: ");

        int position = mAppController.getInt("question_idx", 0);
        mListView.setSelection(position);
        mAdapter.notifyDataSetChanged();

        updateView();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("MyApp", "ExamActivity.onStop: ");
    }

    private void updateView() {
        int total = mExam.getCount();
        int answerd = mExam.getResult().getAnswerdCount();
        int right = mExam.getResult().countRightAnswers();

        float perc_answerd = total == 0 ? 0 : (float) (100 * answerd / total);
        float perc_right = answerd == 0 ? 0 : (float) (100 * right / answerd);

        TextView text_total_value = (TextView) findViewById(R.id.text_value1);
        text_total_value.setText(String.format("%d of %d (%.0f%%)", answerd, total, perc_answerd));

        TextView text_answerd_value = (TextView) findViewById(R.id.text_value2);
        text_answerd_value.setText(String.format("%d of %d (%.0f%%)", right, answerd, perc_right));

        ProgressView progressView = (ProgressView) findViewById(R.id.view_progress);
        progressView.setProgress(mExam);
    }

    private void goQuestion(int idx) {
        mAppController.putInt("question_idx", idx);
        Intent intent = new Intent(this, QuestionActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void onResult(Object parent, Object param) {
        updateView();
    }
}
