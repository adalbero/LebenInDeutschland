package com.adalbero.app.lebenindeutschland.data;

import android.content.Context;
import android.util.Log;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ResultCallback;
import com.adalbero.app.lebenindeutschland.controller.AppController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adalbero on 17/05/2017.
 */

public class ExamSimulate extends ExamDynamic {
    //    List<String> nums = new ArrayList<>();
    List<Integer> idx_all = new ArrayList<>();
    List<Integer> idx_land = new ArrayList<>();

    public ExamSimulate(String name) {
        super(name);

        for (int i = 1; i <= 300; i++) {
            idx_all.add(i);
            if (i <= 10) idx_land.add(i);
        }

        build(null, null);
    }

    @Override
    public void build(Context context, ResultCallback callback) {
        mQuestionNumList = new ArrayList<>();
        String landCode = AppController.getInstance().getSelectedLandCode();

        int TOTAL_ALL = 30;
        int TOTAL_LAND = 3;

        Log.d("MyApp", "ExamSimulate.build: shuffle");

        Collections.shuffle(idx_all);
        Collections.shuffle(idx_land);

        for (int i = 0; i < TOTAL_ALL; i++) {
            mQuestionNumList.add("" + idx_all.get(i));
        }

        if (landCode != null) {
            for (int i = 0; i < TOTAL_LAND; i++) {
                mQuestionNumList.add(landCode + "-" + idx_land.get(i));
            }
        }

        Collections.shuffle(mQuestionNumList);

        if (callback != null) {
            callback.onResult(this, null);
        }
    }

    @Override
    public int getCount() {
        if (super.getCount() == 0) {
            String landCode = AppController.getInstance().getSelectedLandCode();
            return 30 + (landCode == null ? 0 : 3);
        } else {
            return super.getCount();
        }
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_simulate;
    }
}
