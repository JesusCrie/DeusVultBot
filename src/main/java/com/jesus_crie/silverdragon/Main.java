package com.jesus_crie.silverdragon;

public class Main {

    private static SilverDragon bot;

    public static void main(String[] args) {
        Thread.currentThread().setName("SilverDragon-Main#" + Thread.currentThread().getId());

        if (args.length <= 1) {
            System.out.println("You need to provide a token and a secret !");
            return;
        }

        System.out.println("Token provided: " + args[0]);
        System.out.println("Starting...");
        bot = new SilverDragon(args[0], args[1]);
        bot.warmup();
    }

    static SilverDragon getBot() {
        return bot;
    }
}
