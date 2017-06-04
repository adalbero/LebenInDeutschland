package com.adalbero.app.lebenindeutschland.controller;

import com.adalbero.app.lebenindeutschland.data.question.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adalbero on 02/06/2017.
 */

public class Statistics {
    private static final String ANSWER_RIGHT = "r";
    private static final String ANSWER_WRONG = "w";
    private static final String ANSWER_EMPTY = "";

    private static Statistics mInstance;

    private Map<String, Info> mQuestions;

    private Statistics() {
    }

    public static Statistics getInstance() {
        if (mInstance == null) {
            mInstance = new Statistics();
        }

        return mInstance;
    }

    public void update() {
        mQuestions = null;
        getQuestions();
    }
    private Map<String, Info> getQuestions() {
        if (mQuestions == null) {
            mQuestions = new HashMap<>();
        }

        return mQuestions;
    }

    public Info getQuestionStat(String num) {
        Info info = getQuestions().get(num);
        if (info == null) {
            info = new Info(num);
            getQuestions().put(num, info);
        }

        return info;
    }

    public void addAnswer(Question q, String answer) {
        Info info = getQuestionStat(q.getNum());
        info.add(q.getAnswerLetter().equals(answer));
    }


    public float getProgress(List<String> questions) {
        if (questions == null || questions.size() == 0) return 0f;

        float progress = 0f;

        for (String num : questions) {
            if (!getQuestionStat(num).getLast().equals(ANSWER_EMPTY)) {
                progress++;
            }
        }

        return progress / questions.size();
    }

    public float getRating(List<String> questions) {
        if (questions == null || questions.size() == 0) return 0f;

        float rating = 0f;
        int responded = 0;

        for (String num : questions) {
            Info info = getQuestionStat(num);
            if (info.isResponded()) {
                rating += info.getRating();
                responded++;
            }
        }

        return rating / responded;
    }

    public static int getMax() {
        int def = 5;

        try {
            String str = Store.getString("pref.stat.max", "" + def);
            return Integer.parseInt(str);
        } catch (Exception ex) {
            return def;
        }
    }

    public class Info {
        private String num;
        private List<String> mAnswers;

        public Info(String num) {
            this.num = num;
            load();
        }

        public List<String> getAnswers() {
            return mAnswers;
        }

        public void add(boolean answer) {
            String value = (answer ? ANSWER_RIGHT : ANSWER_WRONG);
            getAnswers();

            mAnswers.add(0, value);
            int n = getMax();
            while (mAnswers.size() > n) {
                mAnswers.remove(n);
            }
            save();
        }

        public int getNumResponded() {
            return getAnswers().size();
        }

        public int getNumRight() {
            int ret = 0;
            for (String value : getAnswers()) {
                if (ANSWER_RIGHT.equals(value))
                    ret++;
            }
            return ret;
        }

        public float getRating() {
            int t = getNumResponded();
            int r = getNumRight();

            return (t == 0 ? 0f : (float) r / t);
        }

        public boolean isResponded() {
            return getNumResponded() > 0;
        }

        public boolean isAnswerRight(int i) {
            return ANSWER_RIGHT.equals(getAnswer(i));
        }

        public boolean isAnswerWrong(int i) {
            return ANSWER_WRONG.equals(getAnswer(i));
        }

        public String getAnswer(int i) {
            if (i >= getAnswers().size()) return ANSWER_EMPTY;
            return getAnswers().get(i);
        }

        public String getLast() {
            return getAnswer(0);
        }

        private String getKey(String num, String key) {
            return "stat.question." + num + "." + key;
        }

        public void load() {
            List<String> list = Store.getList(getKey(num, "answers"));
            mAnswers = new ArrayList<>();
            if (list != null) {
                mAnswers.addAll(list);
            }
        }

        public void save() {
            Store.setList(getKey(num, "answers"), getAnswers());
        }
    }

}
