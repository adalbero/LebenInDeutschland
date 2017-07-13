package com.adalbero.app.lebenindeutschland.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.ui.common.StatView;
import com.adalbero.app.lebenindeutschland.controller.Statistics;
import com.adalbero.app.lebenindeutschland.data.exam.Exam;
import com.adalbero.app.lebenindeutschland.data.exam.ExamHeader;

import java.util.List;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class ExamItemAdapter extends ArrayAdapter<Exam> {
    private final LayoutInflater mInflater;

    public Statistics mStat;

    public ExamItemAdapter(Context context, List<Exam> data) {
        super(context, R.layout.exam_item, data);

        mInflater = LayoutInflater.from(context);
        mStat = Statistics.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;

//        if (convertView == null) {
            view = mInflater.inflate(R.layout.exam_item, parent, false);
//        } else {
//            view = convertView;
//        }

        final Exam exam = getItem(position);

        String name = exam.getTitle(true);
        int style = Typeface.NORMAL;
        int color = Color.TRANSPARENT;
        int visible = View.VISIBLE;
        int gravity = Gravity.LEFT;

        if (exam instanceof ExamHeader) {
            style = Typeface.BOLD | Typeface.ITALIC;
            visible = View.GONE;
            name = "\n" + name;
            gravity = Gravity.CENTER_HORIZONTAL;
        } else {
            color = exam.getColor();
        }


        StatView viewStat = view.findViewById(R.id.view_stat);
        viewStat.setExam(exam);
        viewStat.setVisibility(visible);

        View item_view = view.findViewById(R.id.item_view);
        GradientDrawable background = (GradientDrawable) AppController.getCompatDrawable(R.drawable.shape_roundrect);
        background.setColor(color);
        item_view.setBackground(background);

        ImageView btn_icon = view.findViewById(R.id.img_icon);
        btn_icon.setVisibility(visible);
        btn_icon.setImageResource(exam.getIconResource());

        if (exam.isIconColor())
            btn_icon.setColorFilter(Color.TRANSPARENT);

        TextView text_name = view.findViewById(R.id.text_name);
        text_name.setText(name);
        text_name.setTypeface(null, style);
        text_name.setGravity(gravity);

        TextView text_subtitle = view.findViewById(R.id.text_subtitle);
        String subtitle = exam.getSubtitle();
        if (subtitle == null) {
            text_subtitle.setVisibility(View.GONE);
        } else {
            text_subtitle.setVisibility(View.VISIBLE);
            text_subtitle.setText(subtitle);
            text_subtitle.setGravity(gravity);
        }

        View btn_details = view.findViewById(R.id.img_details);
        btn_details.setVisibility(visible);

        return view;
    }

}
