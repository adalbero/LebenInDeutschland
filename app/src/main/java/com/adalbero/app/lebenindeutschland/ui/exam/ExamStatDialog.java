package com.adalbero.app.lebenindeutschland.ui.exam;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ui.common.StatView;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Statistics;
import com.adalbero.app.lebenindeutschland.data.exam.Exam;

import java.util.List;

/**
 * Created by Adalbero on 04/06/2017.
 */

public class ExamStatDialog extends DialogFragment {
    private Exam mExam;

    public void setExamp(Exam exam) {
        mExam = exam;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mExam == null) {
            mExam = AppController.getCurrentExam();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_exam_stat, null);

        initView(view);

        builder.setView(view)
                .setTitle(String.format("Statisics: %s", mExam.getTitle(false)))
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doCloseDialog();
                    }
                });

        return builder.create();
    }

    private void doCloseDialog() {
        getDialog().cancel();
    }

    public Exam getExam() {
        if (mExam == null) {
            mExam = AppController.getCurrentExam();
        }

        return mExam;
    }

    public void initView(View v) {
        Statistics mStat = Statistics.getInstance();
        getExam();
        List<String> questions = mExam.getQuestionList();

        StatView viewStat = v.findViewById(R.id.view_stat);
        viewStat.setExam(mExam);

        int size = mExam.getSize();
        float answerProgress = mStat.getAnswerProgress(questions);
        int answered = (int) (size * answerProgress);
        float rightProgress = mStat.getRightProgress(questions);
        float lastRightProgress = mStat.getLastRightProgress(questions);
        int rating = mStat.getRatingInt(questions);

        TextView viewRating = v.findViewById(R.id.view_rating);
        viewRating.setText(String.format("%d", rating));

        TextView viewHeader = v.findViewById(R.id.view_header);
        viewHeader.setText(String.format("History of %d questions in this list:", size));

        TextView viewLabel1 = v.findViewById(R.id.view_label1);
        TextView viewValue1 = v.findViewById(R.id.view_value1);

        if (answered == size) {
            viewLabel1.setText("All questions answered at least once.");
            viewValue1.setVisibility(View.GONE);
        } else {
            viewLabel1.setText("Never answered:");
            viewValue1.setText(String.format("(%d) %.0f%%", size - answered, 100 - 100 * answerProgress));
        }

        TextView viewLabel2 = v.findViewById(R.id.view_label2);
        TextView viewValue2 = v.findViewById(R.id.view_value2);

        viewLabel2.setText("Last answer right:");
        viewValue2.setText(String.format("(%d) %.0f%%", (int) (answered * lastRightProgress), 100 * lastRightProgress));

        TextView viewLabel3 = v.findViewById(R.id.view_label3);
        TextView viewValue3 = v.findViewById(R.id.view_value3);

        viewLabel3.setText("Right answers:");
        viewValue3.setText(String.format("%.0f%%", 100 * rightProgress));

    }

}
