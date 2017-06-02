package com.adalbero.app.lebenindeutschland;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Clock;
import com.adalbero.app.lebenindeutschland.controller.Dialog;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.controller.Voice;
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
    private ImageButton mBtnPrev;
    private ImageButton mBtnNext;

    private ProgressView mProgressView;

    private Voice mVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question2);

        Exam2 exam = AppController.getCurrentExam();
        mQuestionNumList = exam.getQuestions();

        mResult = new Exam2Result();

        mBtnPrev = (ImageButton) findViewById(R.id.btn_prev);
        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPrev();
            }
        });

        mBtnNext = (ImageButton) findViewById(R.id.btn_next);
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

        mVoice = new Voice(getApplicationContext());
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

        if (mVoice != null) {
            mVoice.shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.question_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_voice);
        if (item != null) {
            boolean enableVoice = Store.getBoolean("beta.pref.voice", false);
            item.setVisible(enableVoice);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tts:
                doSpeak();
                return true;
            case R.id.menu_voice:
                doSpeachRecognize();
                return true;
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

        mBtnPrev.setImageDrawable(ContextCompat.getDrawable(this, idx > 0 ? R.drawable.ic_prev : R.drawable.ic_close));
        mBtnNext.setImageDrawable(ContextCompat.getDrawable(this, idx < count - 1 ? R.drawable.ic_next : R.drawable.ic_close));

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

    private void doSpeak() {
        String text = mQuestion.getSharedContent();
        mVoice.speak(text);
    }

    private void doSpeachRecognize() {
        boolean ok = mVoice.speechRecognizer(this, "de-DE", null);

        if (!ok) {
            Dialog.promptDialog(this, "Speech Recognizer not found in this device");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == Voice.SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Toast.makeText(this, spokenText, Toast.LENGTH_SHORT).show();

            int answer = findAnswer(spokenText);
            if (answer >= 0 && answer <= 3) {
                mQuestionViewHolder.clickAntwort(answer);
            } else if (answer == -2) {
                mVoice.speak("Genauer bitte");
            } else {
                mVoice.speak("Entschuldigung, ich habe dich nicht verstanden");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private int findAnswer(String text) {
        int result = -1;

        if (text == null || text.length() == 0)
            return result;

        text = normalize(text);

        String keywords[] = {"antwort", "nummer"};
        char ch = Character.MIN_VALUE;

        for (int i=0; i<keywords.length; i++) {
            if (text.startsWith(keywords[i])) {
                if (text.length() >= keywords[i].length() + 1) {
                    ch = text.charAt(keywords[i].length());
                    break;
                }
            }
        }

        if (ch != Character.MIN_VALUE) {
            switch (ch) {
                case '1':
                case 'a':
                    return 0;
                case '2':
                case 'b':
                    return 1;
                case '3':
                case 'c':
                    return 2;
                case '4':
                case 'd':
                    return 3;
                default:
                    return result;
            }
        }

        for (int i = 0; i < 4; i++) {
            String opt = mQuestion.getOptions()[i];
            opt = normalize(opt);
            if (opt.indexOf(text) >= 0) {
                if (result != -1) return -2;
                result = i;
            }
        }

        return result;
    }

    private String normalize(String text) {
        text = text.toLowerCase();
        text = text.replaceAll("[\\s.,;:/_()\\[\\]\"'-]+", "");
        return text;
    }

}
