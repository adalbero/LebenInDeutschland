package com.adalbero.app.lebenindeutschland.data.exam;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.Store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class ExamRandom extends Exam {
    private List<Integer> idx_all;
    private List<Integer> idx_land;

    public ExamRandom(String name, String subtitle) {
        super(name, subtitle);

        idx_all = new ArrayList<>();
        idx_land = new ArrayList<>();

        for (int i = 1; i <= 300; i++) {
            idx_all.add(i);
            if (i <= 10) idx_land.add(i);
        }
    }

    @Override
    public String getTitle(boolean showSize) {
        return super.getTitle(showSize);
    }

    @Override
    protected void createQuestionList() {
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

        setQuestionList(list);
    }

    @Override
    public int getIconResource() {
        return R.drawable.ic_simulate;
    }

    @Override
    protected int onGetColorResource() {
        return R.color.colorTest;
    }

}
