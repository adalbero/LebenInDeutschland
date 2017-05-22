package com.adalbero.app.lebenindeutschland;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.data.Exam;
import com.adalbero.app.lebenindeutschland.data.Question;

import java.util.List;
import java.util.Set;

public class QuestionActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, ResultCallback {

    private AppController mAppController;
    private Question mQuestion;
    private Exam mExam;

    List<String> mQuestionNumList;

    private TextView text_header;
    private TextView text_question;
    private RadioButton text_option_a;
    private RadioButton text_option_b;
    private RadioButton text_option_c;
    private RadioButton text_option_d;
    private ImageView image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        mAppController = (AppController) getApplication();

        String listName = mAppController.getString("exam_name", "???");
        mExam = mAppController.getExam(listName);

        mQuestionNumList = mExam.getQuestionNumList();

        text_header = (TextView) findViewById(R.id.text_header);
        text_question = (TextView) findViewById(R.id.text_label1);
        text_option_a = (RadioButton) findViewById(R.id.text_option_a);
        text_option_b = (RadioButton) findViewById(R.id.text_option_b);
        text_option_c = (RadioButton) findViewById(R.id.text_option_c);
        text_option_d = (RadioButton) findViewById(R.id.text_option_d);
        image_view = (ImageView) findViewById(R.id.image_view);

        text_option_a.setOnCheckedChangeListener(this);
        text_option_b.setOnCheckedChangeListener(this);
        text_option_c.setOnCheckedChangeListener(this);
        text_option_d.setOnCheckedChangeListener(this);

        showView();

        mAppController.initAdView(this);

    }

    private void showTagView() {
        LinearLayout tags_view = (LinearLayout)findViewById(R.id.tags_view);
        tags_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTagDialog();
            }
        });

        LinearLayout group_tag = (LinearLayout)findViewById(R.id.group_tag);
        group_tag.removeAllViews();
        Set<String> tags = mQuestion.getTags();
        for (String tag : tags) {
            View view = getLayoutInflater().inflate(R.layout.tag_text, null);
            TextView textView = (TextView)view;
            textView.setText(tag);
            group_tag.addView(textView);
            Space space = new Space(this);
            space.setMinimumWidth(10);
            group_tag.addView(space);
        }
//        TextView text_tag = (TextView)findViewById(R.id.text_tag);
//        String tags = mQuestion.getTags().toString();
//        text_tag.setText(tags);
    }

    private void goTagDialog() {
        TagDialogFragment dialog = new TagDialogFragment();
        dialog.setSelected(mQuestion.getTags());
        dialog.show(getFragmentManager(), "tag");
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
                startActivity(Intent.createChooser(sendIntent, "Share to..."));
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

        String title = String.format("Frage %d von %d", (idx + 1), count);
        getSupportActionBar().setTitle(title);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.scrollTo(0, 0);

        String header = String.format("Frage #%s - %s", mQuestion.getNum(), mQuestion.getTheme());
        text_header.setText(header);
        text_header.setBackgroundColor(getResources().getColor(mQuestion.getAreaColor()));

        style(text_option_a, 0, -1);
        style(text_option_b, 1, -1);
        style(text_option_c, 2, -1);
        style(text_option_d, 3, -1);

        text_question.setText(mQuestion.getQuestion());

        text_option_a.setText(mQuestion.getOptions()[0]);
        text_option_b.setText(mQuestion.getOptions()[1]);
        text_option_c.setText(mQuestion.getOptions()[2]);
        text_option_d.setText(mQuestion.getOptions()[3]);

        String imageName = mQuestion.getImage();
        if (imageName == null) {
            image_view.setVisibility(View.GONE);
        } else {
            Drawable drawable = mAppController.getDrawable(imageName);
            image_view.setImageDrawable(drawable);
            image_view.setVisibility(View.VISIBLE);
        }

        Button btn_prev = (Button) findViewById(R.id.btn_prev);
        Button btn_next = (Button) findViewById(R.id.btn_next);

        btn_prev.setText(idx > 0 ? "Zurück" : "Schließen");
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPrev();
            }
        });

        btn_next.setText(idx < count - 1 ? "Weiter" : "Ende");
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });

        updateProgress();

        String answer = mExam.getResult().getAnswer(num);
        if (answer != null) {
            if (answer.equals("a")) check(text_option_a, true);
            else if (answer.equals("b")) check(text_option_b, true);
            else if (answer.equals("c")) check(text_option_c, true);
            else if (answer.equals("d")) check(text_option_d, true);
        }

        showTagView();
    }

    private void updateProgress() {
        int total = mExam.getCount();
        int answerd = mExam.getResult().getAnswerdCount();
        int right = mExam.getResult().countRightAnswers();

        ProgressView progressView = (ProgressView) findViewById(R.id.view_progress);
        progressView.setProgress(total, right, answerd-right);
    }


    private void showResult() {
        int answer = mQuestion.getAnswer();
        updateProgress();
        style(text_option_a, 0, answer);
        style(text_option_b, 1, answer);
        style(text_option_c, 2, answer);
        style(text_option_d, 3, answer);

    }

    private void check(RadioButton radio, boolean value) {
        radio.setChecked(value);
//        radio.jumpDrawablesToCurrentState();
    }

    private void style(RadioButton btn, int option, int answer) {
        btn.setClickable(answer == -1);
        if (answer == -1) {
            btn.setBackgroundColor(Color.TRANSPARENT);
            if (btn.isChecked()) {
                check(btn, false);
            }
        } else if (answer == option) {
            btn.setBackgroundColor(getResources().getColor(R.color.colorRight));
        } else if (btn.isChecked()) {
            btn.setBackgroundColor(getResources().getColor(R.color.colorWrong));
        }
    }

    private void doClose() {
        this.finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton radioButton, boolean isChecked) {
        if (isChecked) {
            String answer = (String)radioButton.getTag();
            mExam.getResult().setAnswer(mQuestion.getNum(), answer);
            showResult();
        }
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
    public void onResult(Object param) {
        TagDialogFragment dialog = (TagDialogFragment)param;
        mQuestion.setTags(dialog.getSelected());
        showTagView();
    }
}
