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


    public float getRating(List<String> questions) {
        if (questions == null || questions.isEmpty()) return 0f;

        float rating = 0f;

        for (String num : questions) {
            rating += getQuestionStat(num).getRating();
        }

        return rating / questions.size();
    }

    public int getRatingInt(List<String> questions) {
        return (int) (100 * getRating(questions));
    }

    public float getAnswerProgress(List<String> questions) {
        if (questions == null || questions.isEmpty()) return 0f;

        float progress = 0f;

        for (String num : questions) {
            if (!getQuestionStat(num).getLast().equals(ANSWER_EMPTY)) {
                progress++;
            }
        }

        return progress / questions.size();
    }

    public float getRightProgress(List<String> questions) {
        if (questions == null || questions.isEmpty()) return 0f;

        float progress = 0f;
        int answered = 0;

        for (String num : questions) {
            Info info = getQuestionStat(num);
            if (info.isAnswered()) {
                progress += info.getRightProgress();
                answered++;
            }
        }

        return answered == 0 ? 0 : progress / answered;
    }

    public float getLastRightProgress(List<String> questions) {
        if (questions == null || questions.isEmpty()) return 0f;

        float progress = 0f;
        int answered = 0;

        for (String num : questions) {
            Info info = getQuestionStat(num);
            if (info.isAnswered()) {
                progress += info.isAnswerRight(0) ? 1 : 0;
                answered++;
            }
        }

        return answered == 0 ? 0 : progress / answered;
    }

    public static int getHistorySize() {
        int def = 5;

        try {
            String str = Store.getString("pref.stat.max", "" + def);
            return Integer.parseInt(str);
        } catch (Exception ex) {
            return def;
        }
    }

    public static class Info {
        private final String num;
        private List<String> mAnswers;

        public Info(String num) {
            this.num = num;
            load();
        }

        public List<String> getAnswers() {
            return mAnswers;
        }

        public String getAnswer(int i) {
            if (i >= getNumAnswered()) {
                return ANSWER_EMPTY;
            }

            return mAnswers.get(i);
        }

        public void add(boolean answer) {
            String value = (answer ? ANSWER_RIGHT : ANSWER_WRONG);

            mAnswers.add(0, value);
            int n = getHistorySize();
            while (mAnswers.size() > n) {
                mAnswers.remove(n);
            }
            save();
        }

        public int getNumAnswered() {
            return Math.min(getAnswers().size(), getHistorySize());
        }

        public int getNumRight() {
            int n = getNumAnswered();

            int count = 0;
            for (int i = 0; i < n; i++) {
                if (isAnswerRight(i))
                    count++;
            }

            return count;
        }

        public int getNumWrong() {
            return getNumAnswered() - getNumRight();
        }

        public float getRating() {
            int size = getNumAnswered();
            if (size == 0) return 0f;

            int n = getNumAnswered();
            int points = 0;
            int totalWeight = 0;
            for (int i = 0; i < n; i++) {
                String answer = getAnswer(i);
                int weight = size - i;
                totalWeight += weight;
                if (ANSWER_RIGHT.equals(answer)) {
                    points += weight;
                }
            }

            return (float) points / totalWeight;
        }

        public int getRatingInt() {
            return (int) (100 * getRating());
        }

        public float getRightProgress() {
            int t = getNumAnswered();
            int r = getNumRight();

            return (t == 0 ? 0f : (float) r / t);
        }

        public boolean isAnswered() {
            return getNumAnswered() > 0;
        }

        public boolean isAnswerRight(int i) {
            return ANSWER_RIGHT.equals(getAnswer(i));
        }

        public boolean isAnswerWrong(int i) {
            return ANSWER_WRONG.equals(getAnswer(i));
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
