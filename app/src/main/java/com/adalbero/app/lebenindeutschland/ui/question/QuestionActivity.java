package com.adalbero.app.lebenindeutschland.ui.question;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.Analytics;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Clock;
import com.adalbero.app.lebenindeutschland.controller.Dialog;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.controller.Voice;
import com.adalbero.app.lebenindeutschland.data.exam.Exam;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.data.result.ExamResult;
import com.adalbero.app.lebenindeutschland.data.result.ResultInfo;
import com.adalbero.app.lebenindeutschland.ui.common.ProgressView;
import com.adalbero.app.lebenindeutschland.ui.common.ResultCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.List;

public class QuestionActivity extends AppCompatActivity implements ResultCallback {

    private FirebaseAnalytics mFirebaseAnalytics;

    private Question mQuestion;
    private ExamResult mResult;
    private Clock mClock;

    private List<String> mQuestionList;
    private QuestionViewHolder mQuestionViewHolder;
    private String mExamName;
    private TextView mResultView;

    private ImageButton mBtnPrev;
    private ImageButton mBtnNext;

    private ProgressView mProgressView;

    private Voice mVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setContentView(R.layout.activity_question2);

        Exam exam = AppController.getCurrentExam();
        mQuestionList = exam.getQuestionList();
        mExamName = exam.getTitle(false);

        mResult = new ExamResult();

        mBtnPrev = findViewById(R.id.btn_prev);
        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPrev();
            }
        });

        mBtnNext = findViewById(R.id.btn_next);
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });
        mResultView = findViewById(R.id.view_result);

        mClock = new Clock((TextView) findViewById(R.id.view_clock));

        mProgressView = findViewById(R.id.view_progress);

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
    protected void onResume() {
        super.onResume();
//        mFirebaseAnalytics.setCurrentScreen(this, "Question of: " + mExamName, null);
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
                goTranslate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showView() {
        int idx = Store.getQuestionIdx();
        int count = mQuestionList.size();

        if (idx < 0 || idx >= count) {
            idx = 0;
        }

        String num = mQuestionList.get(idx);
        mQuestion = AppController.getQuestionDB().getQuestion(num);

        getSupportActionBar().setTitle(String.format("Question %s", num));
        getSupportActionBar().setSubtitle(String.format("%d of %d", (idx + 1), count));

        ScrollView scrollView = findViewById(R.id.scroll_view);
        scrollView.scrollTo(0, 0);

        mBtnPrev.setImageResource(idx > 0 ? R.drawable.ic_prev : R.drawable.ic_close);
        mBtnNext.setImageResource(idx < count - 1 ? R.drawable.ic_next : R.drawable.ic_close);

        if (mQuestion == null) {
            String exam = AppController.getCurrentExam().getTitle(true);
            String land = Store.getSelectedLandName();
            String msg = String.format("Question==null  count=%d  idx=%d  num=%s  land=%s  exam=%s",
                    count, idx, num, land, exam);

            FirebaseCrashlytics.getInstance().recordException(new NullPointerException((msg)));
        }

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

            Analytics.logExamFinish(mFirebaseAnalytics, mClock, mResult);
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
        final int count = mQuestionList.size();

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

        Analytics.logFeatureSpeak(mFirebaseAnalytics, mQuestion);
    }

    private void goTranslate() {
        String GOOGLE_TRANSLATOR = "com.google.android.apps.translate";
        String text = mQuestion.getSharedContent();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.putExtra("from", "de");
        sendIntent.setType("text/plain");

        String msg;
        try {
            sendIntent.setComponent(new ComponentName(GOOGLE_TRANSLATOR,
                    "com.google.android.apps.translate.TranslateActivity"));

            startActivity(sendIntent);
            msg = null;
        } catch (Exception ex) {
            Dialog.appNotFoundDialog(this, "Google Translator", GOOGLE_TRANSLATOR);
            msg = "App not found";
        }

        Analytics.logFeatureTranslate(mFirebaseAnalytics, msg, mQuestion);
    }

    private void doSpeachRecognize() {
        boolean ok = mVoice.speechRecognizer(this, "de-DE",
                "Sagen Sie: " +
                        "\nder Antworttext" +
                        "\noder: \"Antwort\" (A,B,C,D)" +
                        "\noder: \"Nummer\" (1,2,3,4)");

        if (!ok) {
            String msg = "Speech Recognizer not found in this device";
            Dialog.promptDialog(this, msg);
            Analytics.logFeatureVoice(mFirebaseAnalytics, "E: Speech Recognizer not found", mQuestion);
        }
    }

    private static final String GENAUER_BITTE = "Es gibt mehr als eine antwort. Genauer bitte";
    private static final String ENTSCHULDIGUNG = "Entschuldigung, ich habe dich nicht verstanden";

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == Voice.SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String result[] = findAnswer(results);
            String answer = result[0];
            String spokenText = result[1];
            Toast.makeText(this, spokenText, Toast.LENGTH_SHORT).show();

            String msg;
            if (answer == null) {
                mVoice.speak(ENTSCHULDIGUNG);
                msg = "N";
            } else if (answer.equals(GENAUER_BITTE)) {
                mVoice.speak(GENAUER_BITTE);
                msg = "G";
            } else {
                mQuestionViewHolder.clickAntwort(answer);
                msg = mQuestion.getAnswerLetter().equals(answer) ? "R" : "W";
            }
            Analytics.logFeatureVoice(mFirebaseAnalytics, String.format("%1s: %s", msg, spokenText), mQuestion);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String[] findAnswer(List<String> results) {
        String[] result = new String[2];

        for (String text : results) {
            if (text != null && text.length() > 0) {

                result[1] = text;
                text = normalize(text);

                String[] keywords = {"antwort", "antworte", "nummer"};
                char ch = Character.MIN_VALUE;

                for (int i = 0; i < keywords.length; i++) {
                    if (text.startsWith(keywords[i])) {
                        if (text.length() >= keywords[i].length() + 1) {
                            text = text.substring(keywords[i].length());
                            ch = text.charAt(0);
                            break;
                        }
                    }
                }

                if (text.matches("ein|eins|eine"))
                    ch = '1';

                if (ch != Character.MIN_VALUE) {
                    switch (ch) {
                        case '1':
                        case 'a':
                            result[0] = "a";
                            return result;
                        case '2':
                        case 'b':
                            result[0] = "b";
                            return result;
                        case '3':
                        case 'c':
                            result[0] = "c";
                            return result;
                        case '4':
                        case 'd':
                            result[0] = "d";
                            return result;
                        default:
                            continue; // next text
                    }
                }

                int count = -1;
                for (int i = 0; i < 4; i++) {
                    String opt = mQuestion.getOptions()[i];
                    opt = normalize(opt);
                    if (opt.contains(text)) {
                        if (count != -1) {
                            result[0] = GENAUER_BITTE;
                            return result;
                        }
                        count = i;
                    }
                }

                if (count >= 0) {
                    result[0] = "abcd".substring(count);
                    return result;
                }
            }
        }

        result[0] = null;
        result[1] = results.get(0);

        return result;
    }

    private String normalize(String text) {
        text = text.toLowerCase();
        text = text.replaceAll("[\\s.,;:/_()\\[\\]\"'-]+", "");
        return text;
    }

}
