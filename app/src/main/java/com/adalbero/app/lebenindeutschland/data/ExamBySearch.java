package com.adalbero.app.lebenindeutschland.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ResultCallback;

/**
 * Created by Adalbero on 19/05/2017.
 */

public class ExamBySearch extends ExamDynamic {
    private String terms[];

    public ExamBySearch(String name) {
        super(name);
        String text = getString("terms", null);
        setTerms(text);
    }

    @Override
    public void build(Activity activity, ResultCallback callback) {
        dialog(activity, callback);
    }

    @Override
    protected boolean isFilter(Question q) {
        if (terms == null) return false;

        boolean bExclude = false;
        boolean bInclude = false;

        String text= q.getSharedContent();
        text = normalize(text);

        boolean flag = false;
        for (String term : terms) {
            if (term.length() == 0)
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
    public String getTitle() {
        return super.getTitle() + ": " + getTerms();
    }

    private void setTerms(String text) {
        if (text == null || text.trim().length() == 0) {
            terms = null;
        } else {
            terms = text.split("[ ,;]");

            putString("terms", getTerms());
        }

        filterQuestions();
    }

    private String getTerms() {
        String result = "";

        if (terms != null && terms.length > 0) {
            for (String term : terms) {
                result += term.trim() + " ";
            }
        }

        return result.trim();
    }

    public void dialog(Activity activity, final ResultCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Search for...");

        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(getTerms());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                setTerms(text);
                callback.onResult(ExamBySearch.this, getTerms());
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

    @Override
    public int getIcon() {
        return R.drawable.ic_search;
    }
}
