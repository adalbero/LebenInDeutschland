package com.adalbero.app.lebenindeutschland;

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

import com.adalbero.app.lebenindeutschland.data.Exam;
import com.adalbero.app.lebenindeutschland.data.ExamHeader;

import java.util.List;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class ExamItemAdapter extends ArrayAdapter<Exam> {
    private final LayoutInflater mInflater;

    public ExamItemAdapter(Context context, List<Exam> objects) {
        super(context, R.layout.exam_item, objects);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.exam_item, parent, false);
        } else {
            view = convertView;
        }

        final Exam exam = getItem(position);

        String name = exam.getTitle();
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
            int c = exam.getColor();
            color = parent.getResources().getColor(c);
        }

        View item_view = view.findViewById(R.id.item_view);
        GradientDrawable background = (GradientDrawable)parent.getResources().getDrawable(R.drawable.shape_roundrect);
        background.setColor(color);
        item_view.setBackground(background);

        ImageView btn_icon = (ImageView)view.findViewById(R.id.img_icon);
        btn_icon.setVisibility(visible);
        btn_icon.setImageDrawable(parent.getResources().getDrawable(exam.getIcon()));

        TextView text_name = (TextView) view.findViewById(R.id.text_name);
        text_name.setText(name);
        text_name.setTypeface(null, style);
        text_name.setGravity(gravity);
        View btn_details = view.findViewById(R.id.img_details);
        btn_details.setVisibility(visible);

        return view;
    }
}
