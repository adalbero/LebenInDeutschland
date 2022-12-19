package com.adalbero.app.fragenspider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Adalbero on 18/02/2018.
 */

public class EinbuergerungstestOnlineDe {
    private static final String BASE_URL = "https://www.einbuergerungstest-online.de/fragen/";

    public static List<Question> spider() throws IOException {

        List<Question> list = new ArrayList<>();

        for (int i=1; i<=10; i++) {
            String text = FragenSpider.loadUrl(BASE_URL + i);
            pareQuestions(list, text, null);
        }

        String[] states = {"bb", "be", "bw", "by", "hb", "he", "hh", "mv", "ni", "nw", "rp", "sh", "sl", "sn", "st", "th"};
        for (String state : states) {
            String text = FragenSpider.loadUrl(BASE_URL + state);
            pareQuestions(list, text, state.toUpperCase());
        }

        return list;
    }

    static String REGEX_QUESTION = "(<div class=\"mb-8.*?\".+?</div>)";

    public static void pareQuestions(List<Question> list, String text, String state) {
        Pattern p = Pattern.compile(REGEX_QUESTION, Pattern.DOTALL);
        Matcher m = p.matcher(text);
        int idx = 0;

        while (m.find()) {
            idx++;

            Question q = new Question();

            String str = m.group();

            parseNum(q, str);
            if (state != null) {
                q.num = state + "-" + idx;
            }
            parseText(q, str);
            parseAnswers(q, str);

            list.add(q);
        }
    }

    static String REGEX_NUM = "Frage.*?([0-9]+):";

    public static void parseNum(Question q, String text) {
        Pattern p = Pattern.compile(REGEX_NUM, Pattern.DOTALL);
        Matcher m = p.matcher(text);

        m.find();
        q.num = m.group(1);
     }

    static String REGEX_TEXT = "<strong class=\"font-semibold\">(?:.\\s*<a.+?>)?\\s*(.+?)(?:\\s*</a>)?\\s*</strong>";

    public static void parseText(Question q, String text) {
        Pattern p = Pattern.compile(REGEX_TEXT, Pattern.DOTALL);
        Matcher m = p.matcher(text);

        m.find();
        String str = m.group(1);
        String ELLIPSIS =  "\u2026";
        str = str.replaceAll(ELLIPSIS, "...");
        q.text = str;
    }

//    static String REGEX_ANSWER = "<li>\\S*<span.*?>\\S*(<span.*?>.*?</span>)?\\S*(.*?)\\S*</span>\\S*</li>";
    static String REGEX_ANSWER = "<li.*?>\\s*<span.*?>(\\s*<span.*?>.*?</span>)?\\s*(.*?)\\s*</span>\\s*</li>";

    public static void parseAnswers(Question q, String text) {
        Pattern p = Pattern.compile(REGEX_ANSWER, Pattern.DOTALL);
        Matcher m = p.matcher(text);
        int idx = 0;

        while (m.find()) {
            q.answers[idx] = m.group(2);
            if (m.group(1) != null) {
                q.solution = idx;
            }
            idx ++;
        }
    }

}
