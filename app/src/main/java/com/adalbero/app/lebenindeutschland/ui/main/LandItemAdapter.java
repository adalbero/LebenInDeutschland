package com.adalbero.app.lebenindeutschland.ui.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;

import java.util.List;

/**
 * Created by Adalbero on 07/07/2017.
 */

public class LandItemAdapter extends ArrayAdapter<String> implements View.OnClickListener {
    private final LayoutInflater mInflater;
    private WelcomeDialog dialog;
    private String selectedLand;

    public LandItemAdapter(Context context, List<String> data, WelcomeDialog dialog) {
        super(context, R.layout.land_item, data);

        mInflater = LayoutInflater.from(context);
        this.dialog = dialog;
    }

    public String getLand() {
        return selectedLand;
    }

    public void setLand(String land) {
        selectedLand = land;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.land_item, parent, false);
        } else {
            view = convertView;
        }

        String land = getItem(position);
        String code = land.substring(0, 2);
        String name = land.substring(3);

        view.setOnClickListener(this);

        RadioButton radioView = view.findViewById(R.id.radio_land);
        radioView.setTag(land);
        radioView.setOnClickListener(this);
        if (land.equals(this.selectedLand)) {
            radioView.setChecked(true);
        } else {
            radioView.setChecked(false);
        }

        ImageView iconView = view.findViewById(R.id.icon_land);
        String iconName = "wappen_" + code.toLowerCase();
        int resId = AppController.getResource("drawable", iconName);
        Drawable icon = AppController.getInstance().getResources().getDrawable(resId);
        iconView.setImageDrawable(icon);

        TextView textView = view.findViewById(R.id.text_land);
        textView.setText(name);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.land_item) {
            view = view.findViewById(R.id.radio_land);
        }

        RadioButton v = view.getRootView().findViewWithTag(selectedLand);
        if (v != null && v.isChecked()) {
            v.setChecked(false);
        }

        if (view != null && view instanceof RadioButton) {
            RadioButton radio = (RadioButton)view;

            if (!radio.isChecked()) {
                radio.setChecked(true);
            }

            selectedLand = (String)radio.getTag();

            if (dialog != null) {
                dialog.setEnabled(true);
            }
        }
    }

}

