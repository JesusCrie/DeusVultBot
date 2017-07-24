package com.jesus_crie.deusvult.command;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.exception.CommandException;
import com.jesus_crie.deusvult.response.ResponseUtils;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {

    private String name;
    private String description;
    private List<Long> guildOnly;
    private AccessLevel accessLevel;
    private int context;
    private List<CommandPattern> patterns;

    public Command(String name,
                   String description,
                   List<Long> guildOnly,
                   AccessLevel accesLevel,
                   int context) {
        this.name = name;
        this.description = description;
        if (guildOnly != null)
            this.guildOnly = guildOnly;
        else
            this.guildOnly = new ArrayList<>();
        this.accessLevel = accesLevel;
        this.context = context;
        this.patterns = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isGuildOnly() {
        return !guildOnly.isEmpty();
    }

    public boolean isGuildAuthorized(Guild g) {
        if (!isGuildOnly())
            return true;
        return guildOnly.contains(g.getIdLong());
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public int getContext() {
        return context;
    }

    public List<String> collectNotices() {
        List<String> out = new ArrayList<>();
        patterns.forEach(p -> out.add(StringUtils.PREFIX + name + " " + p.getNotice()));

        return out;
    }

    protected void registerPatterns(CommandPattern... patterns) {
        this.patterns.addAll(Arrays.asList(patterns));
    }

    /**
     * To execute the command
     */
    public void execute(MessageReceivedEvent event, String[] args) throws PermissionException {
        for (CommandPattern pattern : patterns) {
            if (pattern.matchArgs(args)) {
                if (!pattern.execute(event, args)) {
                    ResponseUtils.errorMessage(event.getMessage(), new CommandException(S.RESPONSE_ERROR_COMMAND_CRASH.format()))
                            .send(event.getChannel()).queue();
                }
                return;
            }
        }
        ResponseUtils.errorMessage(event.getMessage(), new CommandException(S.RESPONSE_ERROR_COMMAND_SYNTAX.format()))
                .send(event.getChannel())
                .queue();
    }

    public enum AccessLevel {
        EVERYONE(0), // Default
        ADMIN(1),
        CREATOR(2);

        private int b;
        AccessLevel(int b) {
            this.b = b;
        }

        public boolean superiorOrEqual(AccessLevel level) {
            return level.b >= b;
        }

        public static AccessLevel fromMember(Member m) {
            if (m.getUser().getIdLong() == StringUtils.USER_CREATOR)
                return CREATOR;

            if (m.hasPermission(Permission.ADMINISTRATOR) || m.isOwner())
                return ADMIN;

            return EVERYONE;
        }
    }

    public enum Context {
        MAIN_GUILD(0b0001),
        ALL_GUILD(0b0011),
        PRIVATE(0b0100),
        EVERYWHERE(0b0111);

        public final int b;
        Context(int b) {
            this.b = b;
        }

        public static int calculate(Context... context) {
            int i = 0;
            for (Context c : context)
                i |= c.b;

            return i;
        }

        public static int fromChannel(MessageChannel channel) {
            int i = 0;
            if (channel.getIdLong() == DeusVult.instance().getMainGuild().getIdLong())
                i |= MAIN_GUILD.b;
            switch (channel.getType()) {
                case TEXT:
                case VOICE:
                    i |= ALL_GUILD.b;
                    break;
                default:
                    i |= PRIVATE.b;
            }

            return i;
        }
    }
}
