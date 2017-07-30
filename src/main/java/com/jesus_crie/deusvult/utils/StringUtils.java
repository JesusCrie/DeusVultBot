package com.jesus_crie.deusvult.utils;

import com.jesus_crie.deusvult.Main;
import com.jesus_crie.deusvult.command.Command;
import net.dv8tion.jda.core.entities.User;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtils {

    public static final String PREFIX = ">";
    public static final String VERSION = Main.class.getPackage().getImplementationVersion() == null ? "DEV" : Main.class.getPackage().getImplementationVersion();

    public static final String ICON_INFO = "https://cdn.discordapp.com/attachments/302785106802638848/302790538627776512/sign-info-icon.png";
    public static final String ICON_MUSIC = "https://cdn.discordapp.com/attachments/302785106802638848/318025666199027712/sound-3-icon.png";
    public static final String ICON_HELP = "https://cdn.discordapp.com/attachments/302785106802638848/302793019323580416/sign-question-icon.png";
    public static final String ICON_BED = "https://cdn.discordapp.com/attachments/302785106802638848/302814485440102403/hospital-bed-icon.png";
    public static final String ICON_ERROR = "https://cdn.discordapp.com/attachments/302785106802638848/303136843153539082/sign-error-icon.png";
    public static final String ICON_CHECK = "https://cdn.discordapp.com/attachments/302785106802638848/339750595907026954/sign-check-icon.png";
    public static final String ICON_TERMINAL = "https://cdn.discordapp.com/attachments/302785106802638848/317074381656424459/terminal-icon.png";
    public static final String ICON_DOOR = "https://cdn.discordapp.com/attachments/302785106802638848/317280450811002880/door-icon.png";
    public static final String ICON_GIPHY = "https://cdn.discordapp.com/attachments/302785106802638848/319467975080149003/giphy-logo-6611.png";
    public static final String ICON_CUP = "https://cdn.discordapp.com/attachments/302785106802638848/326739524975722496/cup-512.png";
    public static final String ICON_BELL = "https://cdn.discordapp.com/attachments/302785106802638848/339090136799248385/bell-icon.png";

    public static final String EMOTE_DIAMOND_BLUE = "\uD83D\uDD39";
    public static final String EMOTE_DIAMOND_ORANGE = "\uD83D\uDD38";
    public static final String EMOTE_PREVIOUS = "\u23EA";
    public static final String EMOTE_NEXT = "\u23E9";
    public static final String EMOTE_EXCLAMATION = "\u2754";
    public static final String EMOTE_8BALL = "\uD83C\uDFB1";
    public static final String EMOTE_REVERSE = "\uD83D\uDD04";
    public static final String EMOTE_CACTUS = "\uD83C\uDF35";
    public static final String EMOTE_CONFIRM = "\u2705";
    public static final String EMOTE_DENY = "\u274E";
    public static final String EMOTE_INCOMING_MESSAGE = "\uD83D\uDCE8";
    public static final String EMOTE_MEMO = "\uD83D\uDCDD";
    public static final String EMOTE_TRASH = "\uD83D\uDDD1";
    public static final String EMOTE_DOOR = "\uD83D\uDEAA";

    public static final long USER_CREATOR = 182547138729869314L;
    public static final long ROLE_BOT = 323952614892896261L;

    public static final String GIPHY_KEY = "dc6zaTOxFJmzC";
    public static final String GIPHY_BASE = "http://api.giphy.com/v1/gifs/";
    public static final String GIPHY_RANDOM = GIPHY_BASE + "random";
    public static final String GIPHY_SEARCH = GIPHY_BASE + "search";

    public static final String CONFIG_URL_GENERAL = "http://www.jesus-crie.com/discord/config.json";
    public static final String CONFIG_URL_TEAMS = "http://www.jesus-crie.com/discord/teams.json";
    public static final String CONFIG_URL_SAVE = "http://www.jesus-crie.com/discord/update_config.php";

    public static String stringifyUser(User u) {
        return u.getName() + "#" + u.getDiscriminator();
    }

    public static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String stringifyContext(int context) {
        if ((context & Command.Context.EVERYWHERE.b) == Command.Context.EVERYWHERE.b)
            return Command.Context.EVERYWHERE.name();
        else if ((context & Command.Context.ALL_GUILD.b) == Command.Context.ALL_GUILD.b)
            return Command.Context.ALL_GUILD.name();
        else if ((context & Command.Context.MAIN_GUILD.b) == Command.Context.MAIN_GUILD.b)
            return Command.Context.MAIN_GUILD.name();
        else
            return Command.Context.PRIVATE.name();
    }

    public static String collectStackTrace(Throwable e) {
        final StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }
}
