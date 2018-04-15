package com.adalbero.app.fragenspider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragenSpider {
    private static final String HOME = "C:\\Projetos\\Android2017\\app\\LebenInDeutchland\\FragenSpider";

    public static void main(String[] args) throws Exception {
        System.out.println("BEGIN");

        Map<String, Tags> tagList = loadTags();
        List<Question> questionList = EinbuergerungstestOnline.spider();

        saveFile(questionList, tagList);

        System.out.println("END");
    }

    public static Map<String, Tags> loadTags() throws IOException {
        Map<String, Tags> tagList = new HashMap<>();

        File file = new File(HOME + "\\src\\main\\resources\\tags.csv");
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line;
        while ( (line = reader.readLine()) != null) {
            String vet[] = line.split(";");

            Tags tags = new Tags();
            tags.areaCode = vet[1];
            tags.area = vet[2];
            tags.thema = vet[3];
            tags.image = vet[4];
            if (vet.length > 5)
                tags.tags = vet[5];

            tagList.put(vet[0], tags);
        }

        return tagList;
    }

    public static void saveFile(List<Question> questionList, Map<String, Tags> tagList) throws IOException {
        File file = new File(HOME + "\\out\\questions.txt");
        FileWriter fout = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fout);

        out.write("num;question;a;b;c;d;solution;area_code;area;thema;image;Tags");
        out.newLine();

        for (Question q : questionList) {
            Tags tags = tagList.get(q.num);
            String line = q.toString() + tags.toString();

            System.out.println(line);

            out.write(line);
            out.newLine();
        }
        out.close();
        fout.close();
    }
    public static String loadUrl(String sUrl) throws IOException {
        InputStream stream = null;
        try {
            URL url = new URL(sUrl);
            stream = url.openStream();
            return loadStream(stream);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String loadStream(InputStream stream) throws IOException {
        Reader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
        char[] buffer = new char[1024];
        int count;
        StringBuilder str = new StringBuilder();
        while ((count = reader.read(buffer)) != -1)
            str.append(buffer, 0, count);
        return str.toString();
    }
}
