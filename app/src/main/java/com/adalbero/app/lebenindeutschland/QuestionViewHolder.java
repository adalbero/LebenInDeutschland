package com.adalbero.app.lebenindeutschland;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.data.Exam;
import com.adalbero.app.lebenindeutschland.data.Question;

import java.util.Set;

/**
 * Created by Adalbero on 22/05/2017.
 */

public class QuestionViewHolder implements View.OnClickListener, ResultCallback {
    private View mView;
    private Exam mExam;
    private Question mQuestion;
    private boolean mQuestionPage;

    private TextView mViewHeader;
    private LinearLayout mViewTags;
    private View mViewStatus;
    private TextView mViewQuestion;
    private CheckedTextView mViewOptions[] = new CheckedTextView[4];
    private ImageView mViewImage;
    private RadioGroup mGroupOptions;

    private ResultCallback mCallback;

    private static int COLOR_RIGHT = AppController.getInstance().getBackgroundColor(R.color.colorRight);
    private static int COLOR_WRONG = AppController.getInstance().getBackgroundColor(R.color.colorWrong);

    public QuestionViewHolder(View view, Exam exam, boolean questionPage, ResultCallback callback) {
        mView = view;
        mExam = exam;
        mQuestionPage = questionPage;
        mCallback = callback;

        mViewHeader = (TextView) view.findViewById(R.id.view_header);
        mViewTags = (LinearLayout) view.findViewById(R.id.view_tags);

        mViewStatus = view.findViewById(R.id.view_status);
        mViewQuestion = (TextView) view.findViewById(R.id.view_question);

        mGroupOptions = (RadioGroup) view.findViewById(R.id.group_options);
        mViewOptions[0] = (CheckedTextView) view.findViewById(R.id.view_option_a);
        mViewOptions[1] = (CheckedTextView) view.findViewById(R.id.view_option_b);
        mViewOptions[2] = (CheckedTextView) view.findViewById(R.id.view_option_c);
        mViewOptions[3] = (CheckedTextView) view.findViewById(R.id.view_option_d);
        mViewImage = (ImageView) view.findViewById(R.id.view_image);

        for (CheckedTextView btn : mViewOptions) {
            btn.setOnClickListener(this);
        }
    }


    public View show(Question question) {
        mQuestion = question;
        boolean inline = (AppController.getInstance().getInt("exam.inline", 0) == 1);

        if (mViewHeader != null) {
            String header = String.format("Question #%s - %s", mQuestion.getNum(), mQuestion.getTheme());
            mViewHeader.setText(header);
            mViewHeader.setBackgroundColor(mQuestion.getAreaColor());
        }

        if (mViewTags != null) {
            showTagView();
        }

        if (mViewStatus != null) {
            mViewStatus.setBackgroundColor(mExam.getStatusColor(question.getNum()));
        }

        if (mViewQuestion != null) {
            mViewQuestion.setText(mQuestion.getQuestion());
        }

        if (mQuestionPage || inline) {
            mViewTags.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goTagDialog();
                }
            });

            mGroupOptions.setVisibility(View.VISIBLE);
            for (int i = 0; i < mViewOptions.length; i++) {
                mViewOptions[i].setText(mQuestion.getOptions()[i]);
            }

            showOptions();

            if (mViewImage != null) {
                String imageName = mQuestion.getImage();
                if (imageName == null) {
                    mViewImage.setVisibility(View.GONE);
                } else {
                    Drawable drawable = AppController.getInstance().getDrawable(imageName);
                    mViewImage.setImageDrawable(drawable);
                    mViewImage.setVisibility(View.VISIBLE);
                }
            }

        } else {
            if (mGroupOptions != null)
                mGroupOptions.setVisibility(View.GONE);
            if (mViewImage != null)
                mViewImage.setVisibility(View.GONE);
            if (mViewTags != null)
                mViewTags.setOnClickListener(null);
        }

        return mView;
    }

    private void showOptions() {
        String response = mExam.getResult().getAnswer(mQuestion.getNum());
        String answer = mQuestion.getAnswerLetter();

        for (CheckedTextView btn : mViewOptions) {
            checkOption(btn, answer, response);
        }

    }

    private void checkOption(CheckedTextView btn, String answer, String response) {
        boolean selected = response != null && btn.getTag().equals(response);
        btn.setClickable(response == null);

        int color;
        if (response == null)
            color = Color.TRANSPARENT;
        else if (btn.getTag().equals(answer))
            color = COLOR_RIGHT;
        else if (btn.getTag().equals(response))
            color = COLOR_WRONG;
        else
            color = Color.TRANSPARENT;

        btn.setBackgroundColor(color);
        btn.setChecked(selected);
    }

    private void showResult() {
        if (mCallback != null)
            mCallback.onResult(this, null);

        show(mQuestion);
    }

    private void showTagView() {
        Activity activity = (Activity) mView.getContext();

        LinearLayout group_tag = (LinearLayout) mViewTags.findViewById(R.id.group_tag);
        group_tag.removeAllViews();
        Set<String> tags = mQuestion.getTags();
        for (String tag : tags) {
            View view = activity.getLayoutInflater().inflate(R.layout.tag_text, null);
            TextView textView = (TextView) view;
            textView.setText(tag);
            group_tag.addView(textView);
            Space space = new Space(activity);
            space.setMinimumWidth(10);
            group_tag.addView(space);
        }
    }

    private void goTagDialog() {
        Activity activity = (Activity) mView.getContext();

        TagDialogFragment dialog = new TagDialogFragment();
        dialog.setQuestion(mQuestion, this);
        dialog.show(activity.getFragmentManager(), "tag");
    }


    @Override
    public void onClick(View v) {
        String answer = (String) v.getTag();
        mExam.getResult().setAnswer(mQuestion.getNum(), answer);
        showResult();
    }

    @Override
    public void onResult(Object parent, Object param) {
        if (parent instanceof TagDialogFragment) {
            show(mQuestion);
        }
    }
}
