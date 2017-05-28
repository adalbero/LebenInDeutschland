package com.adalbero.app.lebenindeutschland.data.exam;

import android.app.Activity;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ResultCallback;
import com.adalbero.app.lebenindeutschland.controller.Store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class Exam2Random extends Exam2 {
    private List<Integer> idx_all;
    private List<Integer> idx_land;

    public Exam2Random(String name) {
        super(name);

    }

    @Override
    public void onCreate() {
        idx_all = new ArrayList<>();
        idx_land = new ArrayList<>();

        for (int i = 1; i <= 300; i++) {
            idx_all.add(i);
            if (i <= 10) idx_land.add(i);
        }

        super.onCreate();
    }

    @Override
    public boolean onPrompt(Activity activity, ResultCallback callback) {
        update();
        return false;
    }

    @Override
    protected List<String> onGetQuestions() {
        String key = getParamKey("questions");

        List<String> list = Store.getList(key);

        if (list != null) {
            return list;
        }

        list = random();

        Store.setList(key, list);

        return list;
    }

    private List<String> random() {
        List<String> list = new ArrayList<>();
        String landCode = Store.getSelectedLandCode();

        int TOTAL_ALL = 30;
        int TOTAL_LAND = 3;

        Collections.shuffle(idx_all);
        Collections.shuffle(idx_land);

        for (int i = 0; i < TOTAL_ALL; i++) {
            list.add("" + idx_all.get(i));
        }

        if (landCode != null) {
            for (int i = 0; i < TOTAL_LAND; i++) {
                list.add(landCode + "-" + idx_land.get(i));
            }
        }

        Collections.shuffle(list);

        return list;
    }

    @Override
    public int getSize() {
        update();
        return super.getSize();
    }

    @Override
    protected int onGetIconResource() {
        return R.drawable.ic_simulate;
    }

    @Override
    protected int onGetColorResource() {
        return R.color.colorTest;
    }


}
