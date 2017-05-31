package com.adalbero.app.lebenindeutschland;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Clock;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.exam.Exam2;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.data.result.Exam2Result;
import com.adalbero.app.lebenindeutschland.data.result.ResultInfo;

import java.util.List;
import java.util.Locale;

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

    private TextToSpeech mTTS;

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

        mTTS = new TextToSpeech(this.getApplicationContext(), ttsListener);
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

        if (mTTS != null) {
            mTTS.shutdown();
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
                doTTS();
                return true;
            case R.id.menu_voice:
                displaySpeechRecognizer();
                return true;
            case R.id.menu_share:
                goShare();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doTTS() {
        String text = mQuestion.getSharedContent();
        speak(text);
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

    private static final int SPEECH_REQUEST_CODE = 0;

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE");
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Antwort");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException ex) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Speech Recognizer not found in this device")
                    .setPositiveButton("OK", null);
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Toast.makeText(this, spokenText, Toast.LENGTH_SHORT).show();

            int answer = findAnswer(spokenText);
            if (answer >= 0 && answer <= 3) {
                mQuestionViewHolder.clickAntwort(answer);
            } else if (answer == -2) {
                speak("Genauer bitte");
            } else {
                speak("Entschuldigung, ich habe dich nicht verstanden");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private int findAnswer(String text) {
        if (text.equalsIgnoreCase("antwort 1"))
            return 0;
        if (text.equalsIgnoreCase("antwort 2"))
            return 1;
        if (text.equalsIgnoreCase("antwort 3"))
            return 2;
        if (text.equalsIgnoreCase("antwort 4"))
            return 3;

        int result = 0;
        for (int i = 0; i < 4; i++) {
            String opt = mQuestion.getOptions()[i];
            opt = opt.replaceAll("\\\\s+", "");
            text = text.replaceAll("\\\\s+", "");
            String regex = "(?i)(.*)" + text + "(.*)";
            boolean m = opt.matches(regex);
            if (m)
                result |= 1 << i;
        }

        Log.d("MyApp", "QuestionActivity.findAnswer: result=" + result);

        if (result == 1) return 0;
        if (result == 2) return 1;
        if (result == 4) return 2;
        if (result == 8) return 3;

        if (result != 0) return -2;

        return -1;
    }

    private void speak(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTTS.speak(text, TextToSpeech.QUEUE_ADD, null, "DEFAULT");
        } else {
            mTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    private TextToSpeech.OnInitListener ttsListener =
            new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(final int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        mTTS.setLanguage(Locale.GERMAN);
                    } else {
                        Log.d("MyApp", "Error starting the text to speech engine.");
                    }
                }
            };
}
