package com.adalbero.app.lebenindeutschland.ui.main;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.ui.common.ResultCallback;

import java.util.List;

/**
 * Created by Adalbero on 03/07/2017.
 */

public class WelcomeDialog extends DialogFragment {
    private LandItemAdapter adapter;
    private Button positiveButton;
    private ResultCallback callback;

    public void setCallback(ResultCallback callback) {
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Context context = getActivity();

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_welcome, null);
        initView(view);

        builder.setView(view)
                .setCancelable(false)
                .setTitle("Welcome!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String land = adapter.getLand();
                        if (callback != null) {
                            callback.onResult(WelcomeDialog.this, land);
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            setEnabled(false);
        }
    }

    public void setEnabled(boolean state) {
        positiveButton.setEnabled(state);
    }

    public void initView(View v) {
        List<String> data = AppController.getInstance().getQuestionDB().listDistinctLand();

        adapter = new LandItemAdapter(v.getContext(), data, this);
        ListView listView = v.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "Land: " + view.getTag(), Toast.LENGTH_SHORT).show();
                WelcomeDialog.this.getDialog().cancel();
            }
        });
    }

    private class LandItemAdapter extends ArrayAdapter<String> implements View.OnClickListener {
        private final LayoutInflater mInflater;
        private RadioButton selected = null;
        private WelcomeDialog dialog;

        public LandItemAdapter(Context context, List<String> data, WelcomeDialog dialog) {
            super(context, R.layout.land_item, data);

            mInflater = LayoutInflater.from(context);
            this.dialog = dialog;
        }

        public String getLand() {
            if (selected != null) {
                return (String)selected.getTag();
            }

            return null;
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

            if (view != null && view instanceof RadioButton) {
                RadioButton radio = (RadioButton)view;

                if (selected != null && selected != radio) {
                    selected.setChecked(false);
                }

                if (!radio.isChecked()) {
                    radio.setChecked(true);
                }

                selected = radio;
                dialog.setEnabled(true);
            }
        }
    }

}
