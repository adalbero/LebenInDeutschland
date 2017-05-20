package com.adalbero.app.lebenindeutschland;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.data.Exam;
import com.adalbero.app.lebenindeutschland.data.Question;

import java.util.ArrayList;
import java.util.List;

public class ExamActivity extends AppCompatActivity {

    private List<Question> data = new ArrayList();
    private Exam mExam;
    private ListView mListView;
    private QuestionItemAdapter mAdapter;

    private AppController mAppController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        mAppController =  (AppController)getApplication();

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

        mAdapter = new QuestionItemAdapter(this, data, mExam);

        mListView = (ListView)findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                goQuestion(position);
            }
        });

        mAppController.initAdView(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        int position = mAppController.getInt("question_idx", 0);
        mListView.setSelection(position);
        mAdapter.notifyDataSetChanged();

        updateView();
    }

    private void updateView() {
        int total = mExam.getCount();
        int answerd = mExam.getResult().getAnswerdCount();
        int right = mExam.getResult().countRightAnswers();

        float perc_answerd = (float)(100 * answerd / total);
        float perc_right = answerd == 0 ? 0 : (float)(100 * right / answerd);

        TextView text_total_value = (TextView)findViewById(R.id.text_value1);
        text_total_value.setText(String.format("%d von %d (%.0f%%)", answerd, total, perc_answerd));

        TextView text_answerd_value = (TextView)findViewById(R.id.text_value2);
        text_answerd_value.setText(String.format("%d von %d (%.0f%%)", right, answerd, perc_right));

        ProgressView progressView = (ProgressView) findViewById(R.id.view_progress);
        progressView.setProgress(total, right, answerd-right);
    }

    private void goQuestion(int idx) {
        mAppController.putInt("question_idx", idx);
        Intent intent = new Intent(this, QuestionActivity.class);
        this.startActivity(intent);
    }
}
