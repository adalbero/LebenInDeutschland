package com.adalbero.app.lebenindeutschland.data.exam;

import android.app.Activity;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ResultCallback;
import com.adalbero.app.lebenindeutschland.TagDialogFragment;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.question.Question;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class Exam2Tag extends Exam2 implements ResultCallback {
    private static final String KEY = "pref.tags";

    private Set<String> mTags;
    private ResultCallback mCallback;

    public Exam2Tag(String name) {
        super(name);
    }

    private Set<String> getTags() {
        mTags = new TreeSet<>();
        Set<String> tags = Store.getSet(KEY);
        if (tags != null) {
            mTags.addAll(tags);
        }

        return mTags;
    }

    private void setTags(Set<String> tags) {
        mTags = tags;
        Store.setSet(KEY, tags);
        update();
    }

    @Override
    public String getTitle() {
        String str = getTags().toString();
        str = str.substring(1, str.length() - 1);
        return super.getTitle() + ": " + str;
    }

    @Override
    protected boolean onFilter(Question q) {
        if (mTags != null) {
            for (String tag : mTags) {
                if (q.hasTag(tag)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onUpdate() {
        getTags();
        update();
    }

    @Override
    public boolean onPrompt(Activity activity, ResultCallback callback) {
        dialog(activity, callback);
        return true;
    }

    public void dialog(Activity activity, ResultCallback callback) {
        this.mCallback = callback;
        TagDialogFragment dialog = new TagDialogFragment();
        dialog.setTags(getTags(), this, false);
        dialog.show(activity.getFragmentManager(), "tag");
    }

    @Override
    protected int onGetIconResource() {
        return R.drawable.ic_tag;
    }


    @Override
    public void onResult(Object parent, Object param) {
        if (parent instanceof TagDialogFragment) {
            TagDialogFragment dialog = (TagDialogFragment) parent;
            Set<String> selected = dialog.getSelected();
            setTags(selected);

            update();

            mCallback.onResult(this, getName());
        }
    }
}
