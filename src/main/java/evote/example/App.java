package com.example;

public class App {
    public String greet(String name) {
        if (name == null || name.isBlank()) {
            return "Hello, world!";
        }
        return "Hello, " + name + "!";
    }

    public static void main(String[] args) {
        System.out.println(new App().greet("evote"));
    }
}