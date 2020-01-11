package com.adalbero.app.lebenindeutschland.ui.question;

import android.app.Activity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.Analytics;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Statistics;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.data.result.ExamResult;
import com.adalbero.app.lebenindeutschland.ui.common.ResultCallback;
import com.adalbero.app.lebenindeutschland.ui.common.StatView;
import com.adalbero.app.lebenindeutschland.ui.common.TagDialog;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Set;

/**
 * Created by Adalbero on 22/05/2017.
 */

public class QuestionViewHolder implements View.OnClickListener, ResultCallback {
    private FirebaseAnalytics mFirebaseAnalytics;

    private View mView;
    private Question mQuestion;
    private ExamResult mResult;
    private boolean mQuestionPage;
    private Statistics mStats;

    private TextView mViewHeader;
    private TextView mViewNum;
    private LinearLayout mViewTags;
    private ImageView mViewTagButton;
    private StatView mViewStat;
    private View mViewStatus;
    private TextView mViewQuestion;
    private CheckedTextView mViewOptions[] = new CheckedTextView[4];
    private ImageView mViewImage;
    private ImageView mViewImageAlt;
    private View mGroupOptions;

    private ResultCallback mCallback;
    private boolean hasSpace;

//    private static int COLOR_DEFAULT = Color.TRANSPARENT;
    private static int COLOR_DEFAULT = AppController.getCompatColor(R.color.colorOption);
    private static int COLOR_RIGHT = AppController.getCompatColor(R.color.colorRightLight);
    private static int COLOR_WRONG = AppController.getCompatColor(R.color.colorWrongLight);

    public QuestionViewHolder(View view, ExamResult result, boolean questionPage, ResultCallback callback) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(view.getContext());

        mView = view;
        mResult = result;
        mQuestionPage = questionPage;
        mCallback = callback;
        mStats = Statistics.getInstance();

        mViewHeader = view.findViewById(R.id.view_header);
        mViewNum = view.findViewById(R.id.view_num);
        mViewTags = view.findViewById(R.id.view_tags);
        mViewTagButton = view.findViewById(R.id.img_tag);
        mViewStat = view.findViewById(R.id.view_stat);
        mViewStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goStatDialog();
            }
        });

        mViewStatus = view.findViewById(R.id.view_status);
        mViewQuestion = view.findViewById(R.id.view_question);

        mGroupOptions = view.findViewById(R.id.group_options);
        mViewOptions[1] = view.findViewById(R.id.view_option_b);
        mViewOptions[0] = view.findViewById(R.id.view_option_a);
        mViewOptions[2] = view.findViewById(R.id.view_option_c);
        mViewOptions[3] = view.findViewById(R.id.view_option_d);
        mViewImage = view.findViewById(R.id.view_image);
        mViewImageAlt = view.findViewById(R.id.view_image_alt);

        mViewTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTagDialog();
            }
        });

        for (CheckedTextView btn : mViewOptions) {
            btn.setOnClickListener(this);
        }

        DisplayMetrics displayMetrics = AppController.getDisplayMetrics();

        float h = displayMetrics.heightPixels / displayMetrics.density;
        float w = displayMetrics.widthPixels / displayMetrics.density;

        if (!questionPage && w > h)
            w -= 300;

        hasSpace = w > 600;
    }


    public View show(Question question) {
        if (question == null) {
//            FirebaseCrash.report(new NullPointerException("Question == null"));

            question = Question.EMPTY_QUESTION;
        }

        mQuestion = question;

        boolean inline = Store.getExamInline();

        if (mViewHeader != null) {
            String header = String.format(mQuestion.getTheme());
            mViewHeader.setText(header);
            mViewHeader.setBackgroundColor(mQuestion.getAreaColor());
        }

        if (mViewNum != null) {
            String num = mQuestion.getNum();
            mViewNum.setText(num);
            mViewHeader.setBackgroundColor(mQuestion.getAreaColor());
        }

        if (mViewTags != null) {
            showTagView();
        }

        if (mViewStat != null) {
            mViewStat.setQuestion(mQuestion);
        }

        if (mViewStatus != null) {
            String num = mQuestion.getNum();
            int color = mResult.getStatusColor(num);
            mViewStatus.setBackgroundColor(color);
        }

        if (mViewQuestion != null) {
            mViewQuestion.setText(mQuestion.getQuestion());
        }

        final boolean b = mQuestionPage || inline;

        if (b) {
            mViewTags.setClickable(true);
            mViewTagButton.setVisibility(View.VISIBLE);

            mGroupOptions.setVisibility(View.VISIBLE);
            for (int i = 0; i < mViewOptions.length; i++) {
                mViewOptions[i].setText(mQuestion.getOptions()[i]);
            }

            showOptions();

            if (mViewImage != null) {
                String imageName = mQuestion.getImage();
                if (imageName == null) {
                    mViewImage.setVisibility(View.GONE);
                    mViewImageAlt.setVisibility(View.GONE);
                } else {
                    int resId = AppController.getImageResourceByName(imageName);
                    if (hasSpace) {
                        mViewImageAlt.setImageResource(resId);
                        mViewImageAlt.setVisibility(View.VISIBLE);
                        mViewImage.setVisibility(View.GONE);
                    } else {
                        mViewImage.setImageResource(resId);
                        mViewImage.setVisibility(View.VISIBLE);
                        mViewImageAlt.setVisibility(View.GONE);
                    }
                }
            }

        } else {
            if (mGroupOptions != null)
                mGroupOptions.setVisibility(View.GONE);
            if (mViewImage != null)
                mViewImage.setVisibility(View.GONE);
            if (mViewImageAlt != null)
                mViewImageAlt.setVisibility(View.GONE);
            if (mViewTags != null) {
                mViewTags.setClickable(false);
            }
            if (mViewTagButton != null)
                mViewTagButton.setVisibility(View.GONE);
        }

        return mView;
    }

    private void showOptions() {
        String userResponse = mResult.getAnswer(mQuestion.getNum());
        String correctAnswer = mQuestion.getAnswerLetter();

        for (CheckedTextView btn : mViewOptions) {
            checkOption(btn, correctAnswer, userResponse);
        }

    }

    private void checkOption(CheckedTextView btn, String answer, String response) {
        boolean selected = response != null && btn.getTag().equals(response);
        btn.setClickable(response == null);

        int color;
        if (response == null) {
            color = COLOR_DEFAULT;
        } else if (btn.getTag().equals(answer)) {
            color = COLOR_RIGHT;
        } else if (btn.getTag().equals(response)) {
            color = COLOR_WRONG;
        } else {
            color = COLOR_DEFAULT;
        }

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

        LinearLayout group_tag = mViewTags.findViewById(R.id.group_tag);
        group_tag.removeAllViews();
        Set<String> tags = mQuestion.getTags();
        for (String tag : tags) {
            View view = activity.getLayoutInflater().inflate(R.layout.tag_text, null);
            TextView textView = (TextView) view;
            textView.setText(tag);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            group_tag.addView(textView);
            Space space = new Space(activity);
            space.setMinimumWidth(10);
            group_tag.addView(space);
        }
    }

    private void goTagDialog() {
        Activity activity = (Activity) mView.getContext();

        TagDialog dialog = new TagDialog();
        dialog.setTitle("Tag this question with...");
        Set<String> tags = mQuestion.getTags();
        dialog.setTags(tags, this, true);
        dialog.show(activity.getFragmentManager(), "tag");
    }


    private void goStatDialog() {
        Activity activity = (Activity) mView.getContext();

        QuestionStatDialog dialog = new QuestionStatDialog();
        dialog.setQuestion(mQuestion);
        dialog.show(activity.getFragmentManager(), "stat");

        Analytics.logFeatureQuestionStat(mFirebaseAnalytics, mQuestion);
    }

    public void clickAntwort(String antwort) {
        String list = "abcd";

        if (antwort != null && antwort.length() > 0) {
            char ch = antwort.charAt(0);

            for (int i = 0; i < list.length(); i++) {
                if (list.charAt(i) == ch) {
                    clickAntwort(i);
                    return;
                }
            }
        }
    }


    public void clickAntwort(int idx) {
        if (mViewOptions[idx].isClickable()) {
            mViewOptions[idx].callOnClick();
        }
    }

    @Override
    public void onClick(View v) {
        String answer = (String) v.getTag();
        String num = mQuestion.getNum();

        mResult.setAnswer(num, answer);
        mStats.addAnswer(mQuestion, answer);

        Analytics.logQuestionAnswer(mFirebaseAnalytics, mResult, mQuestion);

        showResult();
    }

    @Override
    public void onResult(Object parent, Object param) {
        if (parent instanceof TagDialog) {
            TagDialog dialog = (TagDialog) parent;
            Set<String> tags = dialog.getSelected();
            mQuestion.setTags(tags);

            Analytics.logFeatureTagged(mFirebaseAnalytics, mQuestion);

            show(mQuestion);
        }
    }
}
