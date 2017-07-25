package com.jesus_crie.deusvult;

public class Main {

    private static DeusVult bot;

    public static void main(String[] args) {
        Thread.currentThread().setName("DeusVult-Main");

        if (args.length <= 1) {
            System.out.println("You need to provide a token and a secret !");
            return;
        }

        System.out.println("Token provided: " + args[0]);
        System.out.println("Starting...");
        bot = new DeusVult(args[0], args[1]);
        bot.warmup();
    }

    static DeusVult getBot() {
        return bot;
    }
}
