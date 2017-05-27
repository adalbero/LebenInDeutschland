package com.adalbero.app.lebenindeutschland.data;

import android.app.Activity;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ResultCallback;
import com.adalbero.app.lebenindeutschland.TagDialogFragment;
import com.adalbero.app.lebenindeutschland.controller.Store;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Adalbero on 26/05/2017.
 */

public class ExamByTag extends ExamDynamic implements ResultCallback {
    private Set<String> tags;
    private ResultCallback callback;

    public ExamByTag(String name) {
        super(name);
    }

    @Override
    protected boolean isFilter(Question q) {
        for (String tag : tags) {
            if (q.hasTag(tag)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getTitle() {
        String str = tags.toString();
        str = str.substring(1, str.length()-1);
        return super.getTitle() + ": " + str;
    }

    @Override
    public void init() {
        tags = Store.getTagTerms();
        if (tags == null)
            tags = new HashSet<>();

        super.init();
    }

    @Override
    public void build(Activity activity, ResultCallback callback) {
        dialog(activity, callback);
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_tag;
    }

    public void dialog(Activity activity, final ResultCallback callback) {
        this.callback = callback;
        TagDialogFragment dialog = new TagDialogFragment();
        dialog.setTags(tags, this, false);
        dialog.show(activity .getFragmentManager(), "tag");
    }

    @Override
    public void onResult(Object parent, Object param) {
        if (parent instanceof TagDialogFragment) {
            TagDialogFragment dialog = (TagDialogFragment)parent;
            Store.setTagTerms(dialog.getSelected());
            init();
            callback.onResult(this, null);
        }
    }

}
