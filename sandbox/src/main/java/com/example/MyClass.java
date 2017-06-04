package com.example;

import java.util.ArrayList;
import java.util.List;

public class MyClass {
    public static void main(String[] args) {
        System.out.println("BEGIN");


        List<Integer> list = new ArrayList<>();

        for (int i=0; i<10; i++) {
            list.add(0, i);
            System.out.println(list.toString());
        }

        int n = 3;
        while (list.size() > n) {
            list.remove(n);
            System.out.println(list.toString());
        }

        System.out.println("END");
    }

}
