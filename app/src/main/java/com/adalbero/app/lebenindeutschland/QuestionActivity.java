package com.adalbero.app.lebenindeutschland;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.data.Exam;
import com.adalbero.app.lebenindeutschland.data.Question;

import java.util.List;

import static com.adalbero.app.lebenindeutschland.R.id.btn_next;

public class QuestionActivity extends AppCompatActivity implements ResultCallback {

    private AppController mAppController;
    private Question mQuestion;
    private Exam mExam;

    private List<String> mQuestionNumList;
    private QuestionViewHolder mQuestionViewHolder;

    private Button mBtnPrev;
    private Button mBtnNext;

    private ProgressView mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question2);

        mAppController = (AppController) getApplication();

        String listName = mAppController.getString("exam_name", "???");
        mExam = mAppController.getExam(listName);

        mQuestionNumList = mExam.getQuestionNumList();

        mBtnPrev = (Button) findViewById(R.id.btn_prev);
        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPrev();
            }
        });

        mBtnNext = (Button) findViewById(btn_next);
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });

        mProgressView = (ProgressView) findViewById(R.id.view_progress);

        View contentView = findViewById(R.id.item_question);
        mQuestionViewHolder = new QuestionViewHolder(contentView, mExam, true, this);

        mAppController.initAdView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        showView();
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
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mQuestion.getSharedContent());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Send to..."));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showView() {
        final int idx = mAppController.getInt("question_idx", -1);
        final int count = mQuestionNumList.size();

        String num = mQuestionNumList.get(idx);
        mQuestion = mAppController.getQuestionDB().findByNum(num);

        String title = String.format("Question %d of %d", (idx + 1), count);
        getSupportActionBar().setTitle(title);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.scrollTo(0, 0);

        mBtnPrev.setText(idx > 0 ? "Previous" : "Close");
        mBtnNext.setText(idx < count - 1 ? "Next" : "Finish");

        mQuestionViewHolder.show(mQuestion);

        updateProgress();
//
//        showTagView();
    }

    private void updateProgress() {
        if (mProgressView != null) {
            mProgressView.setProgress(mExam);
        }
    }

    private void doClose() {
        this.finish();
    }

    private void goPrev() {
        final int idx = mAppController.getInt("question_idx", -1);

        if (idx > 0) {
            mAppController.putInt("question_idx", idx - 1);
            showView();
        } else {
            doClose();
        }
    }

    private void goNext() {
        final int idx = mAppController.getInt("question_idx", -1);
        final int count = mQuestionNumList.size();

        if (idx < count - 1) {
            mAppController.putInt("question_idx", idx + 1);
            showView();
        } else {
            doClose();
        }
    }

    @Override
    public void onResult(Object parent, Object param) {
        if (parent instanceof QuestionViewHolder) {
            updateProgress();
        }
    }
}
