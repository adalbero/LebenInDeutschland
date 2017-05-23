package com.adalbero.app.lebenindeutschland.data;

import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;

import java.util.HashSet;
import java.util.Set;

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

    private Set<String> tags;

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

//        question.tags = new HashSet<>();
        question.loadTags();

        question.autoTag();

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

    public String getAnswerLetter() {
        return Character.toString((char)('a' + answer));
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
        int colorRes = 0;
        if (areaCode.equals("politik"))
            colorRes = R.color.colorArea1;
        else if (areaCode.equals("gesellschaft"))
            colorRes = R.color.colorArea2;
        else if (areaCode.equals("geschichte"))
            colorRes = R.color.colorArea3;
        else if (areaCode.equals("land"))
            colorRes = R.color.colorLand;
        else
            colorRes = R.color.colorArea0;

        return ContextCompat.getColor(AppController.getInstance(), colorRes);
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

    public void autoTag() {
        if (getImage() != null)
            addTag("image");
    }

    public void addTag(String tag) {
        if (tags == null) return;

        tags.add(tag);
        saveTags();
    }

    public void setTags(Set<String> t) {
        if (tags == null) return;

        tags = new HashSet<>(t);
        saveTags();
    }

    public Set<String> getTags() {
        return tags;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public void loadTags() {
        String key = getNum() + ".tags";
        String value = AppController.getInstance().getString(key, "");

        tags = new HashSet<>();
        String items[] = value.split(",");
        for (String item : items) {
            if (item.trim().length() > 0)
                tags.add(item.trim());
        }

    }

    public void saveTags() {
        String key = getNum() + ".tags";
        String value = tags.toString();
        value = value.substring(1, value.length()-1);
        AppController.getInstance().putString(key, value);
    }

}
