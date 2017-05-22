package com.adalbero.app.lebenindeutschland;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.adalbero.app.lebenindeutschland.controller.AppController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Adalbero on 20/05/2017.
 */

public class TagDialogFragment extends DialogFragment {

    ResultCallback mListener;

    public Set<String> selected;
    private TextView newTag;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ResultCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ResultCallback");
        }
    }

    public void setSelected(Set<String> s) {
        selected = new HashSet<>(s);
    }

    public Set<String> getSelected() {
        return selected;
    }

    public void initView(View v) {
        Set<String> tags = AppController.getInstance().getQuestionDB().getAllTags();
        List<String> data = new ArrayList(tags);

        TagItemAdapter adapter = new TagItemAdapter(v.getContext(), data, selected);
        ListView listView = (ListView)v.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        newTag = (TextView)v.findViewById((R.id.new_tag));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_tag, null);
        initView(view);

        builder.setView(view)
                .setTitle("Tags")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        String text = newTag.getText().toString();
                        if (text != null && text.length() > 0) {
                            selected.add(text.trim());
                        }
                        mListener.onResult(TagDialogFragment.this);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        TagDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
