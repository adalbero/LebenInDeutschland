package com.adalbero.app.lebenindeutschland.data.exam;

import android.app.Activity;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.question.Question;
import com.adalbero.app.lebenindeutschland.ui.common.ResultCallback;
import com.adalbero.app.lebenindeutschland.ui.common.TagDialog;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class ExamTag extends Exam implements ResultCallback {
    public static final String KEY = "pref.tags";

    private Set<String> mTags;
    private ResultCallback mCallback;

    public ExamTag(String name) {
        super(name);
    }

    private Set<String> getTags() {
        if (mTags == null) {
            mTags = Store.getSet(KEY);
            if (mTags == null) {
                mTags = new TreeSet<>();
            }
        }

        return mTags;
    }

    private void setTags(Set<String> tags) {
        mTags = tags;
        Store.setSet(KEY, tags);

        invalidateQuestionList();
    }

    @Override
    public String getQualification() {
        String str = getTags().toString();
        str = str.substring(1, str.length() - 1);

        return str;
    }

    @Override
    public String getTitle(boolean showSize) {
        String str = getQualification();
        return super.getTitle(showSize) + ": " + str;
    }

    @Override
    protected boolean onFilterQuestion(Question q) {
        Set<String> tags = getTags();
        if (tags != null) {
            for (String tag : tags) {
                if (q.hasTag(tag)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean onPrompt(Activity activity, ResultCallback callback) {
        dialog(activity, callback);
        return true;
    }

    public void dialog(Activity activity, ResultCallback callback) {
        this.mCallback = callback;
        TagDialog dialog = new TagDialog();
        dialog.setTitle("Search for questions with tags:");
        dialog.setTags(getTags(), this, false);
        dialog.show(activity.getFragmentManager(), "tag");
    }

    @Override
    protected int onGetIconResource() {
        return R.drawable.ic_tag;
    }


    @Override
    public void onResult(Object parent, Object param) {
        if (parent instanceof TagDialog) {
            TagDialog dialog = (TagDialog) parent;
            Set<String> selected = dialog.getSelected();
            setTags(selected);

            mCallback.onResult(this, getName());
        }
    }
}
