package com.adalbero.app.lebenindeutschland;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.controller.Statistics;
import com.adalbero.app.lebenindeutschland.data.question.Question;

/**
 * Created by Adalbero on 03/06/2017.
 */

public class StatQuestionDialogFragment extends DialogFragment {
    private Question mQuestion;
    private Statistics mStat;

    public void setQuestion(Question question) {
        mQuestion = question;
        mStat = Statistics.getInstance();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_stat, null);

        initView(view);

        builder.setView(view)
                .setTitle(String.format("Statisics: Question %s", mQuestion.getNum()))
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        StatQuestionDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    public void initView(View v) {
        Statistics.Info info = mStat.getQuestionStat(mQuestion.getNum());
        int n = mStat.getMax();
        boolean isResponded = info.isResponded();
        boolean lastRight = info.isAnswerRight(0);

        StatView viewStat = (StatView)v.findViewById(R.id.view_stat);
        viewStat.setQuestion(mQuestion);

        TextView viewText = (TextView)v.findViewById(R.id.view_text);

        TextView viewLabel1 = (TextView)v.findViewById(R.id.view_label1);
        TextView viewValue1 = (TextView)v.findViewById(R.id.view_value1);

        TextView viewLabel2 = (TextView)v.findViewById(R.id.view_label2);
        TextView viewValue2 = (TextView)v.findViewById(R.id.view_value2);

        if (n > 1) {
            viewText.setText(String.format("History of the last %d responses:", n));
        } else {
            viewText.setText("History of the last response:");
        }

        if (n > 1 && isResponded) {
            viewLabel1.setText("Right answers:");
            viewValue1.setText(String.format("%d of %d (%.0f%%)", info.getNumRight(), info.getNumResponded(), 100*info.getRating()));
        } else {
            viewLabel1.setVisibility(View.GONE);
            viewValue1.setVisibility(View.GONE);
        }

        if (isResponded) {
            viewLabel2.setText("Last answer:");

            viewValue2.setText(lastRight ? "Right" : "Wrong");
            viewValue2.setTextColor(ContextCompat.getColor(getActivity(), lastRight ? R.color.colorRightDark : R.color.colorWrongDark));
        } else {
            viewLabel2.setText("Question not responded yet");
            viewValue2.setVisibility(View.GONE);
        }

    }

}
