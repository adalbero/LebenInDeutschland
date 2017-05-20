package com.adalbero.app.lebenindeutschland.data;

import android.util.Log;

import com.adalbero.app.lebenindeutschland.R;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class Question {
    private String num;
    private String question;
    private String[] options;
    private int answer;
    private String areaCode;
    private String area;
    private String theme;
    private String image;

    public static Question parse(String line) {
        Question question = new Question();
        String vet[] = line.split(";");

        int idx = 0;
        try {
            question.setNum(vet[idx++]);
            question.setQuestion(vet[idx++]);
            String options[] = new String[4];
            options[0] = vet[idx++];
            options[1] = vet[idx++];
            options[2] = vet[idx++];
            options[3] = vet[idx++];
            question.setOptions(options);
            question.setAnswer(parseAnswer(vet[idx++]));
            question.setAreaCode(vet[idx++]);
            question.setArea(vet[idx++]);
            question.setTheme(vet[idx++]);
            question.setImage(vet[idx++]);
        } catch (Exception ex) {
            Log.e("MyApp", "Question.parse: " + ex.getMessage(), ex);
        }

        return question;
    }

    private static int parseAnswer(String value) {
        return value.charAt(0) - 'a';
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public int getAreaColor() {
        if (areaCode.equals("politik"))
            return R.color.colorArea1;
        else if (areaCode.equals("gesellschaft"))
            return R.color.colorArea2;
        else if (areaCode.equals("geschichte"))
            return R.color.colorArea3;
        else if (areaCode.equals("land"))
            return R.color.colorLand;
        else
            return R.color.colorArea0;
    }

    public String getImage() {
        return image.equals("-") ? null : image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSharedContent() {
        String result = this.getQuestion() + "\n";
        result += "a) " + this.getOptions()[0] + "\n";
        result += "b) " + this.getOptions()[1] + "\n";
        result += "c) " + this.getOptions()[2] + "\n";
        result += "d) " + this.getOptions()[3] + "\n";

        return result;
    }



}
