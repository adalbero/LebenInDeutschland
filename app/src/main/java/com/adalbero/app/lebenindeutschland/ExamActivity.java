package com.adalbero.app.lebenindeutschland;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.data.result.Exam2Result;

import java.util.ArrayList;
import java.util.List;

public class ExamActivity extends AppCompatActivity implements ResultCallback {

    private List<Question> data;
    private Exam2Result mResult;

    private ListView mListView;
    private QuestionItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        Exam2 exam = AppController.getCurrentExam();
        mResult = new Exam2Result();

        List<String> questions = exam.getQuestions();

        String title = exam.getTitle();
        getSupportActionBar().setTitle(title);

        data = new ArrayList();
        if (questions != null) {
            for (String questionNum : questions) {
                Question q = AppController.getQuestionDB().findByNum(questionNum);
                data.add(q);
            }
        }

        mAdapter = new QuestionItemAdapter(this, data, mResult, this);

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

        int position = Store.getQuestionIdx();
        mListView.setSelection(position);

        mAdapter.notifyDataSetChanged();

        updateResult();
    }

    private void updateResult() {
        int total = mResult.getCount();
        int answerd = mResult.getAnswerdCount();
        int right = mResult.countRightAnswers();

        float perc_answerd = total == 0 ? 0 : (float) (100 * answerd / total);
        float perc_right = answerd == 0 ? 0 : (float) (100 * right / answerd);

        TextView text_total_value = (TextView) findViewById(R.id.text_value1);
        text_total_value.setText(String.format("%d of %d (%.0f%%)", answerd, total, perc_answerd));

        TextView text_answerd_value = (TextView) findViewById(R.id.text_value2);
        text_answerd_value.setText(String.format("%d of %d (%.0f%%)", right, answerd, perc_right));

        ProgressView progressView = (ProgressView) findViewById(R.id.view_progress);
        progressView.setProgress(mResult);
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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Toast.makeText(this, "Restore state", Toast.LENGTH_SHORT).show();
    }

}
