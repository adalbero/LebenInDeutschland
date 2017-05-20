package com.adalbero.app.lebenindeutschland;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.data.Exam;
import com.adalbero.app.lebenindeutschland.data.Question;

import java.util.List;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class QuestionItemAdapter extends ArrayAdapter<Question> {
    private final LayoutInflater mInflater;
    private Exam mExam;

    public QuestionItemAdapter(Context context, List<Question> objects, Exam exam) {
        super(context, R.layout.question_item, objects);

        mInflater = LayoutInflater.from(context);
        mExam = exam;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.question_item, parent, false);
        } else {
            view = convertView;
        }

        Question question = getItem(position);

        View view_status = view.findViewById(R.id.view_status);
        int status = mExam.getResult().getAnswerStatus(question.getNum());
        int colorStatus = ContextCompat.getColor(getContext(),
                status == 1 ? R.color.colorRight
                        : status == 0 ? R.color.colorWrong
                        : R.color.colorNotAnswerd);
        view_status.setBackgroundColor(colorStatus);

        TextView text_header = (TextView) view.findViewById(R.id.text_header);
        String header = String.format("Frage #%s - %s", question.getNum(), question.getTheme());
        text_header.setText(header);
        text_header.setBackgroundColor(parent.getResources().getColor(question.getAreaColor()));

        TextView text_question = (TextView) view.findViewById(R.id.text_question);
        text_question.setText(question.getQuestion());

        return view;
    }

}
