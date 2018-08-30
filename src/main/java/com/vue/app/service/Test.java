package com.vue.app.service;

import java.util.Random;

public class Test {
    public static void main(String[] args) {
        Random r = new Random();

        for (int i = 0; i < 20; i++) {
            System.out.println(r.nextInt(100 - 1) + 1);
        }
    }
}
