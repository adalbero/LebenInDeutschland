package com.adalbero.app.lebenindeutschland.data;

import com.adalbero.app.lebenindeutschland.controller.AppController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adalbero on 19/05/2017.
 */

public class ResultExam {
    private Map<String, String> answerList;

    public ResultExam() {
        reset();
    }

    public void reset() {
        answerList = new HashMap<>();
    }

    public int countRightAnswers() {
        return AppController.getInstance().countRightAnswers(answerList);
    }

    public void setAnswer(String num, String answer) {
        answerList.put(num, answer);
    }

    public String getAnswer(String num) {
        return answerList.get(num);
    }

    public int getAnswerdCount() {
        return answerList.size();
    }

    public int getAnswerStatus(String num) {
        String answer = answerList.get(num);
        if (answer == null) return -1;

        Question q = AppController.getInstance().getQuestionDB().findByNum(num);

        if (answer.charAt(0)-'a' == q.getAnswer())
            return 1;
        else
            return 0;
    }
}
