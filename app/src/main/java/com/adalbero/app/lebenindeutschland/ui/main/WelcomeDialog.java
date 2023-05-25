package com.adalbero.app.lebenindeutschland.ui.main;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;
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

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_land, null);
        initView(view);

        builder.setView(view)
                .setCancelable(false)
                .setTitle("Welcome!")
                .setPositiveButton("OK", (dialog, id) -> {
                    String land = adapter.getLand();
                    Store.setLand(land);
                    if (callback != null) {
                        callback.onResult(WelcomeDialog.this, land);
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
            setEnabled(adapter.getLand() != null);
        }
    }

    public void setEnabled(boolean state) {
        positiveButton.setEnabled(state);
    }

    public void initView(View v) {
        List<String> data = AppController.getQuestionDB().listDistinctLand();
        String land = Store.getLand();

        adapter = new LandItemAdapter(v.getContext(), data, this);
        adapter.setLand(land);
        ListView listView = v.findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }


}
