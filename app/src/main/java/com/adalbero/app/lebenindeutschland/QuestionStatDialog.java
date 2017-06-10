package com.adalbero.app.lebenindeutschland;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Statistics;
import com.adalbero.app.lebenindeutschland.data.question.Question;

/**
 * Created by Adalbero on 03/06/2017.
 */

public class QuestionStatDialog extends DialogFragment {
    private Question mQuestion;

    public void setQuestion(Question question) {
        mQuestion = question;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String num = savedInstanceState.getString("question.num", "1");
            mQuestion = AppController.getQuestionDB().getQuestion(num);
        }

        Context context = getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_question_stat, null);

        initView(view);

        builder.setView(view)
                .setTitle(String.format("Statisics: Question %s", mQuestion.getNum()))
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        QuestionStatDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    public void initView(View v) {
        Statistics mStat = Statistics.getInstance();

        Statistics.Info info = mStat.getQuestionStat(mQuestion.getNum());
        int n = mStat.getHistorySize();
        boolean isAnswered = info.isAnswered();
        boolean lastRight = info.isAnswerRight(0);
        float rating = info.getRating();

        StatView viewStat = (StatView)v.findViewById(R.id.view_stat);
        viewStat.setQuestion(mQuestion);

        TextView viewRating = (TextView)v.findViewById(R.id.view_rating);
        viewRating.setText(String.format("Rating: %.0f", 100*rating));

        TextView viewText = (TextView)v.findViewById(R.id.view_header);

        TextView viewLabel1 = (TextView)v.findViewById(R.id.view_label1);
        TextView viewValue1 = (TextView)v.findViewById(R.id.view_value1);

        TextView viewLabel2 = (TextView)v.findViewById(R.id.view_label2);
        TextView viewValue2 = (TextView)v.findViewById(R.id.view_value2);

        if (n > 1) {
            viewText.setText(String.format("History of the last %d answers:", n));
        } else {
            viewText.setText("History of the last answer:");
        }

        if (n > 1 && isAnswered) {
            viewLabel1.setText("Right answers:");
            viewValue1.setText(String.format("%d of %d (%.0f%%)", info.getNumRight(), info.getNumAnswered(), 100*info.getRightProgress()));
        } else {
            viewLabel1.setVisibility(View.GONE);
            viewValue1.setVisibility(View.GONE);
        }

        if (isAnswered) {
            viewLabel2.setText("Last answer:");

            viewValue2.setText(lastRight ? "Right" : "Wrong");
            viewValue2.setTextColor(ContextCompat.getColor(getActivity(), lastRight ? R.color.colorRightDark : R.color.colorWrongDark));
        } else {
            viewLabel2.setText("Question not answered yet");
            viewValue2.setVisibility(View.GONE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("question.num", mQuestion.getNum());
    }

}
