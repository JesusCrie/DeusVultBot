package com.jesus_crie.silverdragon.utils;

public class F {

    public static String bold(String s) {
        return "**" + s + "**";
    }

    public static String italic(String s) {
        return "*" + s + "*";
    }

    public static String under(String s) {
        return "__" + s + "__";
    }

    public static String strike(String s) {
        return "--" + s + "--";
    }

    public static String code(String s) {
        return "`" + s + "`";
    }

    public static String codeBlock(String lang, String s) {
        return "```" + lang + "\n" + s + "```";
    }

    public static String codeBlock(String s) {
        return "```" + s + "```";
    }
}
