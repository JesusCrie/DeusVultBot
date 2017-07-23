package com.jesus_crie.deusvult.utils;

import net.dv8tion.jda.core.entities.User;

public class StringUtils {

    public static final String PREFIX = ">";
    public static final String VERSION = "ALPHA 0.0.1";

    public static final String ICON_INFO = "https://cdn.discordapp.com/attachments/302785106802638848/302790538627776512/sign-info-icon.png";
    public static final String ICON_MUSIC = "https://cdn.discordapp.com/attachments/302785106802638848/318025666199027712/sound-3-icon.png";
    public static final String ICON_HELP = "https://cdn.discordapp.com/attachments/302785106802638848/302793019323580416/sign-question-icon.png";
    public static final String ICON_BED = "https://cdn.discordapp.com/attachments/302785106802638848/302814485440102403/hospital-bed-icon.png";
    public static final String ICON_ERROR = "https://cdn.discordapp.com/attachments/302785106802638848/303136843153539082/sign-error-icon.png";
    public static final String ICON_CHECK = "https://cdn.discordapp.com/attachments/302785106802638848/317008503392829443/sign-check-icon.png";
    public static final String ICON_TERMINAL = "https://cdn.discordapp.com/attachments/302785106802638848/317074381656424459/terminal-icon.png";
    public static final String ICON_DOOR = "https://cdn.discordapp.com/attachments/302785106802638848/317280450811002880/door-icon.png";
    public static final String ICON_GIPHY = "https://cdn.discordapp.com/attachments/302785106802638848/319467975080149003/giphy-logo-6611.png";
    public static final String ICON_CUP = "https://cdn.discordapp.com/attachments/302785106802638848/326739524975722496/cup-512.png";
    public static final String ICON_8BALL = "https://discordapp.com/assets/f73a8294c3ac519665af224d98bf411e.svg";

    public static final String EMOJI_DIAMOND_BLUE = "\uD83D\uDD39";
    public static final String EMOJI_DIAMOND_ORANGE = "\uD83D\uDD38";
    public static final String EMOJI_PREVIOUS = "\u23EA";
    public static final String EMOJI_NEXT = "\u23E9";
    public static final String EMOJI_EXCLAMATION = "\u2754";
    public static final String EMOJI_8BALL = "\uD83C\uDFB1";

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
}
