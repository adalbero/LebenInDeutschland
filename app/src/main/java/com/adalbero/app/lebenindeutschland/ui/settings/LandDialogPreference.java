package com.adalbero.app.lebenindeutschland.ui.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.ui.main.LandItemAdapter;

import java.util.List;

/**
 * Created by Adalbero on 07/07/2017.
 */

public class LandDialogPreference extends DialogPreference {
    private LandItemAdapter adapter;
    private String land;

    public LandDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_land);

        land = Store.getLand();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {

        super.onPrepareDialogBuilder(builder);
    }

    @Override
    public CharSequence getSummary() {
        if (land != null)
            return land.substring(3);
        else
            return "no land selected";
    }

    @Override
    protected void onBindDialogView(View view) {
        initView(view);

        super.onBindDialogView(view);
    }

    public void initView(View v) {
        final Context context = v.getContext();
        List<String> data = AppController.getQuestionDB().listDistinctLand();

        adapter = new LandItemAdapter(context, data, null);
        adapter.setLand(land);
        ListView listView = v.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        TextView textView = v.findViewById(R.id.text_land);
        textView.setText("");
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        land = adapter.getLand();

        if (positiveResult) {
            Store.setLand(land);
            callChangeListener(land);
        }
    }
}
