package com.jesus_crie.deusvult.command;

import com.jesus_crie.deusvult.DeusVult;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandPattern {

    private List<Argument> arguments;
    private BiPredicate<MessageReceivedEvent, List<Object>> action;

    public CommandPattern(Argument[] args, BiPredicate<MessageReceivedEvent, List<Object>> action) {
        if (args != null && args.length > 0)
            arguments = Arrays.asList(args);
        else
            arguments = new ArrayList<>();
        this.action = action;
    }

    public boolean hasArgument() {
        return !arguments.isEmpty();
    }

    public boolean matchArgs(String[] args) {
        if (!hasArgument())
            return true;

        if (args.length < arguments.size())
            return false;

        for (int i = 0; i < args.length; i++) {
            if (i >= arguments.size()) {
                if (arguments.get(arguments.size() - 1).isRepeatable()) {
                    if (!arguments.get(arguments.size() - 1).match(args[i]))
                        return false;
                    continue;
                } else
                    return true;
            }

            if (!arguments.get(i).match(args[i]))
                return false;
        }

        return true;
    }

    /**
     * This supposed that {@link #matchArgs(String[])} has returned True.
     */
    private List<Object> collectArgs(String[] args) {
        List<Object> out = new ArrayList<>();

        if (!hasArgument())
            return out;

        for (int i = 0; i < args.length; i++) {
            if (i >= arguments.size()) {
                if (arguments.get(arguments.size() - 1).isRepeatable()) {
                    out.add(arguments.get(arguments.size() - 1).map(args[i]));
                    continue;
                } else
                    return out;
            }

            out.add(arguments.get(i).map(args[i]));
        }

        return out;
    }

    public boolean execute(MessageReceivedEvent event, String[] args) {
        return action.test(event, collectArgs(args));
    }

    public static class Argument implements Cloneable {

        // Static content
        public static final Argument NUMBER = new Argument("(?<value>[0-9]{0,18})",
                matcher -> Long.valueOf(matcher.group("value")));

        public static final Argument STRING = new Argument("(?<value>[\\S]+)",
                matcher -> matcher.group("value"));

        public static final Argument WORD_ONLY_LETTERS = new Argument("(?<value>[\\w]+)",
                matcher -> matcher.group("value"));

        public static final Argument URL_AS_STRING = new Argument("(?<url>(?:https?:\\/\\/){1}[a-z\\d.-]+(?:\\/[a-z\\d.-]*)*)",
                matcher -> matcher.group("url"));

        public static final Argument URL = new Argument("(?<url>(?:https?:\\/\\/){1}[a-z\\d.-]+(?:\\/[a-z\\d.-]*)*)",
                matcher -> {
                    try {
                        return new URL(matcher.group("url"));
                    } catch (MalformedURLException ignore) { return null; } // Will never happen (if i m good)
                });

        public static final Argument MAIL = new Argument("(?<mail>[a-z\\d.\\-\\+]+@[a-z\\d-.]+\\.[a-z]{2,6})",
                matcher -> matcher.group("mail"));

        public static final Argument USER = new Argument("(?:<@!?(?<id>[0-9]*)>|(?<name>\\p{Graph}*)#(?<discriminator>[0-9]{4}))",
                matcher -> {
                    if (matcher.group("id") != null && !matcher.group("id").isEmpty())
                        return DeusVult.instance().getJda().getUserById(matcher.group("id"));
                    else
                        return DeusVult.instance().getUserByNameDiscriminator(matcher.group("name"), matcher.group("discriminator"));
                });

        public static final Argument CHANNEL = new Argument("<#(?<id>[0-9]*)>",
                matcher -> DeusVult.instance().getJda().getTextChannelById(matcher.group("id")));

        public static final Argument ROLE = new Argument("<@&(?<id>[0-9]*)>",
                matcher -> DeusVult.instance().getJda().getRoleById(matcher.group("id")));

        public static final Argument EMOJI_CUSTOM = new Argument("<:[a-z_]*:(?<id>[0-9]*)>",
                matcher -> DeusVult.instance().getJda().getEmoteById(matcher.group("id")));

        /**
         * Build an argument for a specific {@link java.lang.String String}
         */
        public static Argument forString(String s) {
            return new Argument(s, matcher -> s);
        }

        private Pattern pattern;
        private Function<Matcher, Object> mapper;
        private boolean repeatable = false;

        public Argument(String pattern, Function<Matcher, Object> mapper) {
            this.pattern = Pattern.compile("^" + pattern + "$", Pattern.UNICODE_CHARACTER_CLASS + Pattern.CASE_INSENSITIVE);
            this.mapper = mapper;
        }

        /**
         * Repeatable is only apply if the argument is the last.
         * @param repeatable - Is repeatable ?
         */
        public Argument setRepeatable(boolean repeatable) {
            this.repeatable = repeatable;
            return this;
        }

        public boolean isRepeatable() {
            return repeatable;
        }

        public boolean match(String toCheck) {
            return pattern.matcher(toCheck).matches();
        }

        /**
         * This supposed that {@link #match(String)} has returned True.
         */
        public Object map(String match) {
            Matcher m = pattern.matcher(match);
            m.find();
            return mapper.apply(m);
        }

        @Override
        public Argument clone() {
            try {
                return (Argument) super.clone();
            } catch (Exception ignore) { return null; } // Will never happen
        }
    }
}
