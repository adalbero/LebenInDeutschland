package com.adalbero.app.lebenindeutschland;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Clock;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.data.result.Exam2Result;
import com.adalbero.app.lebenindeutschland.data.result.ResultInfo;

import java.util.List;

public class QuestionActivity extends AppCompatActivity implements ResultCallback {

    private Question mQuestion;
    private Exam2Result mResult;
    private Clock mClock;

    private List<String> mQuestionNumList;
    private QuestionViewHolder mQuestionViewHolder;

    private TextView mResultView;
    private Button mBtnPrev;
    private Button mBtnNext;

    private ProgressView mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question2);

        Exam2 exam = AppController.getCurrentExam();
        mQuestionNumList = exam.getQuestions();

        mResult = new Exam2Result();

        mBtnPrev = (Button) findViewById(R.id.btn_prev);
        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPrev();
            }
        });

        mBtnNext = (Button) findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });
        mResultView = (TextView) findViewById(R.id.view_result);

        mClock = new Clock((TextView) findViewById(R.id.view_clock));

        mProgressView = (ProgressView) findViewById(R.id.view_progress);

        View contentView = findViewById(R.id.item_question);
        mQuestionViewHolder = new QuestionViewHolder(contentView, mResult, true, this);

        AppController.initAdView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mResult.reset();

        showView();
        mClock.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mClock.pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                goShare();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goShare() {
        String text = mQuestion.getSharedContent();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Send to..."));
    }

    private void showView() {
        final int idx = Store.getQuestionIdx();
        final int count = mQuestionNumList.size();

        String num = mQuestionNumList.get(idx);
        mQuestion = AppController.getQuestionDB().findByNum(num);

        String title = String.format("Question %d of %d", (idx + 1), count);
        getSupportActionBar().setTitle(title);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.scrollTo(0, 0);

        mBtnPrev.setText(idx > 0 ? "Previous" : "Close");
        mBtnNext.setText(idx < count - 1 ? "Next" : "Finish");

        mQuestionViewHolder.show(mQuestion);

        updateResult();
    }

    private void updateResult() {
        ResultInfo resultInfo = mResult.getResult();

        if (mProgressView != null) {
            mProgressView.setProgress(resultInfo);
        }

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

    private void doClose() {
        this.finish();
    }

    private void goPrev() {
        final int idx = Store.getQuestionIdx();

        if (idx > 0) {
            Store.setQuestionIdx(idx - 1);
            showView();
        } else {
            doClose();
        }
    }

    private void goNext() {
        final int idx = Store.getQuestionIdx();
        final int count = mQuestionNumList.size();

        if (idx < count - 1) {
            Store.setQuestionIdx(idx + 1);
            showView();
        } else {
            doClose();
        }
    }

    @Override
    public void onResult(Object parent, Object param) {
        if (parent instanceof QuestionViewHolder) {
            updateResult();
        }
    }

}
