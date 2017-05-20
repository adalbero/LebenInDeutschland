package com.adalbero.app.lebenindeutschland.data;

import android.app.AlertDialog;
import android.content.Context;
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
    public void build(Context context, ResultCallback callback) {
        dialog(context, callback);
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
        return super.getName() + ": " + getTerms() + " (" + getCount() + ")";
    }

    private void setTerms(String text) {
        if (text == null) {
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

    public void dialog(Context context, final ResultCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Search for...");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(getTerms());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                setTerms(text);
                callback.onResult(getTerms());
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
