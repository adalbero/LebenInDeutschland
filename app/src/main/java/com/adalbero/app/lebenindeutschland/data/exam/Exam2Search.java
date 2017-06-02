package com.adalbero.app.lebenindeutschland.data.exam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ResultCallback;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.question.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Adalbero on 28/05/2017.
 */

public class Exam2Search extends Exam2 {
    private static final String KEY = "pref.search";

    private List<String> mTerms;

    public Exam2Search(String name) {
        super(name);
    }

    private String getTermsString() {
        String str = "";
        List<String> terms = getTerms();
        for (String item : terms) {
            str += item + " ";
        }
        str = str.trim();

        return str;
    }

    private List<String> getTerms() {
        mTerms = new ArrayList<>();
        List<String> terms = Store.getList(KEY);
        if (terms != null) {
            mTerms.addAll(terms);
        }

        return mTerms;
    }

    private void setTermsString(String str) {
        String vet[] = str.split("[ ,;]");
        List<String> terms = Arrays.asList(vet);
        setTerms(terms);
    }

    private void setTerms(List<String> terms) {
        mTerms = terms;
        Store.setList(KEY, terms);
        update();
    }

    @Override
    public String getTitle() {
        String str = getTermsString();
        return super.getTitle() + ": " + str;
    }

    @Override
    protected int onGetIconResource() {
        return R.drawable.ic_search;
    }

    @Override
    protected boolean onFilter(Question q) {
        if (mTerms == null || mTerms.size() == 0) return false;

        boolean bExclude = false;
        boolean bInclude = false;

        String text = q.getSharedContent();
        text = normalize(text);

        boolean flag = false;
        for (String term : mTerms) {
            if (term == null || term.length() == 0)
                continue;

            if (term.charAt(0) == '-') {
                term = term.substring(1);
                bExclude = true;
            } else if (term.charAt(0) == '+') {
                term = term.substring(1);
                bInclude = true;
            }

            term = normalize(term);
            if (text.indexOf(term) >= 0) {
                if (bExclude) return false;
                flag = true;
            } else {
                if (bInclude) return false;
            }
        }

        return flag;
    }

    private String normalize(String text) {
        text = text.replaceAll("[ä]", "a");
        text = text.replaceAll("[ö]", "o");
        text = text.replaceAll("[ü]", "u");
        text = text.toLowerCase();

        return text;
    }

    @Override
    public void onUpdate() {
        getTerms();
        update();
    }

    @Override
    public boolean onPrompt(Activity activity, ResultCallback callback) {
        dialog(activity, callback);
        return true;
    }

    public void dialog(Activity activity, final ResultCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Search for...");

        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(getTermsString());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                setTermsString(text);
                callback.onResult(Exam2Search.this, getName());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


}
