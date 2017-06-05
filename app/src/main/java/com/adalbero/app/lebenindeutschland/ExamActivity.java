package com.adalbero.app.lebenindeutschland;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Clock;
import com.adalbero.app.lebenindeutschland.controller.Statistics;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.data.result.Exam2Result;
import com.adalbero.app.lebenindeutschland.data.result.ResultInfo;

import java.util.ArrayList;
import java.util.List;

public class ExamActivity extends AppCompatActivity implements ResultCallback {

    private Exam2 mExam;
    private Exam2Result mResult;
    private Clock mClock;

    private ListView mListView;
    private TextView mClockView;
    private TextView mResultView;
    private StatView mStatView;

    private QuestionItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exam);

        mExam = AppController.getCurrentExam();

        String title = mExam.getTitle(false);
        getSupportActionBar().setTitle(title);

        mResult = new Exam2Result();

        List<String> questions = mExam.getQuestions();
        List<Question> data = new ArrayList();
        if (questions != null) {
            for (String questionNum : questions) {
                Question q = AppController.getQuestionDB().findByNum(questionNum);
                data.add(q);
            }
        }

        mAdapter = new QuestionItemAdapter(this, data, mResult, this);

        mClockView = (TextView) findViewById(R.id.view_clock);
        mClock = new Clock(mClockView);

        mResultView = (TextView) findViewById(R.id.view_result);

        mStatView = (StatView) findViewById(R.id.view_stat);
        mStatView.setExam(mExam);
        mStatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goStatDialog();
            }
        });

        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                boolean inline = Store.getExamInline();
                if (!inline)
                    goQuestion(position);
            }
        });

        AppController.initAdView(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exam_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean inline = Store.getExamInline();

        menu.findItem(R.id.menu_expand).setVisible(!inline);
        menu.findItem(R.id.menu_collapse).setVisible(inline);

        return super.onPrepareOptionsMenu(menu);
    }

    private void setInline(boolean inline) {
        Store.setExamInline(inline);

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
    protected void onStart() {
        super.onStart();

        mResult.reset();

        int position = Store.getQuestionIdx();
        mListView.setSelection(position);

        mAdapter.notifyDataSetChanged();

        updateResult();

        mClock.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mClock.pause();
    }

    private void updateStat() {
        mStatView.setExam(mExam);
        TextView viewRating = (TextView) findViewById(R.id.view_rating);
        float rating = Statistics.getInstance().getRating(mExam.getQuestions());

        viewRating.setText(String.format("%.0f", 100*rating));
    }

    private void updateResult() {
        ResultInfo resultInfo = mResult.getResult();

        updateStat();

        TextView text_total_value = (TextView) findViewById(R.id.text_value1);
        text_total_value.setText(String.format("%d of %d (%.0f%%)", resultInfo.answered, resultInfo.total, resultInfo.getAnsweredPerc()));

        TextView text_answerd_value = (TextView) findViewById(R.id.text_value2);
        text_answerd_value.setText(String.format("%d of %d (%.0f%%)", resultInfo.right, resultInfo.answered, resultInfo.getRightPerc()));

        ProgressView progressView = (ProgressView) findViewById(R.id.view_progress);
        progressView.setProgress(resultInfo);

        if (resultInfo.isFinished()) {
            mClock.stop();

            if (resultInfo.isPass()) {
                mResultView.setText("Pass");
                mResultView.setTextColor(ContextCompat.getColor(this, R.color.colorRightDark));
            } else {
                mResultView.setText("Fail");
                mResultView.setTextColor(ContextCompat.getColor(this, R.color.colorWrongDark));
            }
            mResultView.setVisibility(View.VISIBLE);
        }
    }

    private void goQuestion(int idx) {
        Store.setQuestionIdx(idx);
        Intent intent = new Intent(this, QuestionActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void onResult(Object parent, Object param) {
        updateResult();
    }

    private void goStatDialog() {
        ExamStatDialog dialog = new ExamStatDialog();
        dialog.setExamp(mExam);
        dialog.show(this.getFragmentManager(), "stat");
    }



}
