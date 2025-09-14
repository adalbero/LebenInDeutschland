package com.adalbero.app.lebenindeutschland.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;

import com.adalbero.app.lebenindeutschland.R;

import java.util.List;
import java.util.Set;

/**
 * Created by Adalbero on 20/05/2017.
 */

public class TagItemAdapter extends ArrayAdapter<String> {
    private final LayoutInflater mInflater;
    private final Set<String> mSelected;

    public TagItemAdapter(Context context, List<String> objects, Set<String> selected) {
        super(context, R.layout.tag_item, objects);

        mInflater = LayoutInflater.from(context);

        mSelected = selected;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.tag_item, parent, false);
        } else {
            view = convertView;
        }

        String text = getItem(position);

        final CheckedTextView textView = view.findViewById(R.id.text1);
        textView.setOnClickListener(v -> {
            if (textView.isChecked()) {
                textView.setChecked(false);
                mSelected.remove(textView.getText().toString());
            } else {
                textView.setChecked(true);
                mSelected.add(textView.getText().toString());
            }
        });
        textView.setText(text);
        textView.setChecked(mSelected.contains(text));

        return view;
    }

}
