package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordReactCommand extends Command {

    private final HashMap<Character, String> emoteMain;
    private final HashMap<Character, String> emoteSecondary;

    public WordReactCommand() {
        super("wr",
                "Ecrit un mot avec des réactions sur le dernier message.",
                null,
                AccessLevel.EVERYONE,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommand, "<mot>")
        );

        emoteMain = new HashMap<>();
        emoteSecondary = new HashMap<>();

        emoteMain.put('a', "\uD83C\uDDE6");
        emoteMain.put('b', "\uD83C\uDDE7");
        emoteMain.put('c', "\uD83C\uDDE8");
        emoteMain.put('d', "\uD83C\uDDE9");
        emoteMain.put('e', "\uD83C\uDDEA");
        emoteMain.put('f', "\uD83C\uDDEB");
        emoteMain.put('g', "\uD83C\uDDEC");
        emoteMain.put('h', "\uD83C\uDDED");
        emoteMain.put('i', "\uD83C\uDDEE");
        emoteMain.put('j', "\uD83C\uDDEF");
        emoteMain.put('k', "\uD83C\uDDF0");
        emoteMain.put('l', "\uD83C\uDDF1");
        emoteMain.put('m', "\uD83C\uDDF2");
        emoteMain.put('n', "\uD83C\uDDF3");
        emoteMain.put('o', "\uD83C\uDDF4");
        emoteMain.put('p', "\uD83C\uDDF5");
        emoteMain.put('q', "\uD83C\uDDF6");
        emoteMain.put('r', "\uD83C\uDDF7");
        emoteMain.put('s', "\uD83C\uDDF8");
        emoteMain.put('t', "\uD83C\uDDF9");
        emoteMain.put('u', "\uD83C\uDDFA");
        emoteMain.put('v', "\uD83C\uDDFB");
        emoteMain.put('w', "\uD83C\uDDFC");
        emoteMain.put('x', "\uD83C\uDDFD");
        emoteMain.put('y', "\uD83C\uDDFE");
        emoteMain.put('z', "\uD83C\uDDFF");

        emoteSecondary.put('a', "\uD83C\uDD70");
        emoteSecondary.put('o', "\uD83C\uDD7E");
        emoteSecondary.put('p', "\uD83C\uDD7F");
        emoteSecondary.put('m', "\u24C2");
        emoteSecondary.put('i', "\u2139");
    }

    private boolean onCommand(MessageReceivedEvent event, List<Object> args) {
        String word = String.join("", args.stream()
            .map(Object::toString)
            .toArray(String[]::new));

        Message toReact = event.getChannel().getHistory().retrievePast(1).complete().get(0);
        List<Character> used = new ArrayList<>();

        for (char c : word.toCharArray()) {
            if (!used.contains(c)) {
                used.add(c);
                toReact.addReaction(emoteMain.get(c)).queue();
            } else if (emoteSecondary.keySet().contains(c))
                toReact.addReaction(emoteSecondary.get(c)).queue();
        }

        return true;
    }
}
