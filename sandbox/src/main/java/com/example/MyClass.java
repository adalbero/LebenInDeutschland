package com.example;

public class MyClass {
    public static void main(String[] args) {
        System.out.println("BEGIN");

        String text = normalize("This is a text    with-strange     caracteres, and/or it is true.");
        String find = normalize("is a");

        boolean f = text.indexOf(find) >= 0;

        System.out.println("Result = " + f);


        System.out.println("END");
    }

    private static String normalize(String text) {
        text = text.toLowerCase();
        text = text.replaceAll("[\\s,.;:/-]+", ".");

        System.out.println("text = " + text);

        return text;
    }
}
