package com.adalbero.app.lebenindeutschland.ui.exam;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ui.common.ResultCallback;

/**
 * Created by Adalbero on 07/06/2017.
 */

public class SortDialog extends DialogFragment implements View.OnClickListener {
    private ResultCallback mCallback;

    private int mSortMethod;
    private int mReverse = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AppDialog);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_sort, null);

        view.findViewById(R.id.view_sort_by_num).setOnClickListener(this);
        view.findViewById(R.id.view_sort_by_rating).setOnClickListener(this);
        view.findViewById(R.id.view_sort_by_shuffle).setOnClickListener(this);
        view.findViewById(R.id.view_sort_reverse).setOnClickListener(this);

        builder.setView(view)
                .setTitle("Sort list by ...")
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    if (mCallback != null) {
                        mCallback.onResult("SORT", mReverse * mSortMethod);
                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> SortDialog.this.getDialog().cancel());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialog1 -> {
            enablePositiveButton(false);
            hideReverse(true);
        });
        return dialog;
    }

    public void setCallback(ResultCallback callback) {
        mCallback = callback;
    }

    public void enablePositiveButton(boolean enable) {
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enable);
    }

    public void hideReverse(boolean hide) {
        getDialog().findViewById(R.id.view_sort_reverse).setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof RadioButton) {
            try {
                mSortMethod = Integer.parseInt((String) v.getTag());
                hideReverse(mSortMethod == 0);

                enablePositiveButton(true);
            } catch (NumberFormatException ex) {
                // ignore
            }
        } else if (v instanceof CheckBox) {
            mReverse = ((CheckBox) v).isChecked() ? -1 : 1;
        }
    }
}
