package com.adalbero.app.lebenindeutschland.data.exam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.ui.common.ResultCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Adalbero on 28/05/2017.
 */

public class ExamSearch extends Exam {
    public static final String KEY = "pref.search";

    private List<String> mTerms;

    public ExamSearch(String name, String subtitle) {
        super(name, subtitle);
    }

    private List<String> getTerms() {
        if (mTerms == null) {
            mTerms = Store.getList(KEY);
            if (mTerms == null) {
                mTerms = new ArrayList<>();
            }
        }

        return mTerms;
    }

    private void setTerms(List<String> terms) {
        mTerms = terms;
        Store.setList(KEY, mTerms);

        invalidateQuestionList();
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

    private void setTermsString(String str) {
        String[] vet = str.split("[ ,;]");
        List<String> terms = Arrays.asList(vet);
        setTerms(terms);
    }

    @Override
    public String getQualification() {
        return getTermsString();
    }

    @Override
    public String getTitle(boolean showSize) {
        String str = getQualification();
        return super.getTitle(showSize) + ": " + str;
    }

    @Override
    public int getIconResource() {
        return R.drawable.ic_search;
    }

    @Override
    protected boolean onFilterQuestion(Question q) {
        List<String> terms = getTerms();
        if (terms == null || terms.size() == 0) return false;

        boolean bExclude = false;
        boolean bInclude = false;
        boolean bByNum = false;

        String text = q.getSharedContent();
        text = normalize(text);

        boolean result = false;
        for (String term : terms) {
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
            if (text.contains(term)) {
                if (bExclude) return false;
                result = true;
            } else {
                if (bInclude) return false;
            }

            if (q.getNum().equals(term)) {
                result = true;
            }

            if (q.getTags().contains(term)) {
                result = true;
            }
        }

        return result;
    }

    private String normalize(String text) {
        text = text.replaceAll("[ä]", "a");
        text = text.replaceAll("[ö]", "o");
        text = text.replaceAll("[ü]", "u");
        text = text.toLowerCase();

        return text;
    }

    @Override
    public boolean onPrompt(Activity activity, ResultCallback callback) {
        dialog(activity, callback);
        return true;
    }

    public void dialog(Activity activity, final ResultCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppDialog);
        builder.setTitle("Search for...");

        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(getTermsString());
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String text = input.getText().toString();
            setTermsString(text);
            callback.onResult(ExamSearch.this, getName());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }


}
