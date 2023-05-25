package com.adalbero.app.lebenindeutschland.data.exam;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.Store;
import com.adalbero.app.lebenindeutschland.data.question.QuestionComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adalbero on 10/07/2017.
 */

public class ExamExercise extends Exam {
    private List<Integer> idx_all;
    private List<Integer> idx_land;

    public ExamExercise(String name, String subtitle) {
        super(name, subtitle);

        idx_all = new ArrayList<>();
        idx_land = new ArrayList<>();

        for (int i = 1; i <= 300; i++) {
            idx_all.add(i);
            if (i <= 10) idx_land.add(i);
        }
    }


    @Override
    protected void createQuestionList() {
        // All 300 geral
        List<String> all310 = new ArrayList<>();
        for (int i = 1; i <= 300; i++) {
            all310.add("" + i);
        }

        // All 10 land
        String landCode = Store.getSelectedLandCode();
        if (landCode != null) {
            for (int i = 1; i <= 10; i++) {
                all310.add(landCode + "-" + i);
            }
        }

        // shuffle
        Collections.shuffle(all310);

        // order by rating
        Collections.sort(all310, new QuestionComparator(2, 1));

        int size = Store.getExerciseSize();

        // pick 20 first
        List<String> list = new ArrayList<>();
        for (int i=0; i<size; i++) {
            list.add(all310.get(i));
        }

        // shuffle
        Collections.shuffle(list);

        setQuestionList(list);
    }

    @Override
    public int getIconResource() {
        return R.drawable.ic_exercise;
    }

    @Override
    protected int onGetColorResource() {
        return R.color.colorExercise;
    }

}

