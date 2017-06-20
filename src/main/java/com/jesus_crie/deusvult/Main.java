package com.jesus_crie.deusvult;

public class Main {

    private static DeusVult bot;

    public static void main(String[] args) {
        if (args.length <= 0) {
            System.out.println("You need to provide a token !");
            return;
        }

        System.out.println("Token provided: " + args[0]);
        System.out.println("Starting...");
        bot = new DeusVult(args[0]);
    }

    static DeusVult getBot() {
        return bot;
    }
}
