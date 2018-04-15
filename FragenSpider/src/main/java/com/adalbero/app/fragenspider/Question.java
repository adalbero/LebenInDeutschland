package com.adalbero.app.fragenspider;

/**
 * Created by Adalbero on 18/02/2018.
 */

public class Question {
    public String num;
    public String text;
    public String answers[] = new String[4];
    public int solution;

    public String toString() {
        String str = num  + ";" + text  + ";";
        for (int i=0; i<answers.length; i++) {
            str += answers[i] + ";";
        }

        str += "abcd".charAt(solution);

        return str;
    }
}
