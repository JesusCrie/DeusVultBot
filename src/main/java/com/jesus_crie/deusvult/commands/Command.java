package com.jesus_crie.deusvult.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Command {

    protected List<String> aliases;
    private String shortDesc;
    protected String description;
    protected List<String> examples;
    protected List<String> guildOnly;
    protected AccessLevel accessLevel;
    protected boolean hidden;
    protected List<ChannelType> contextAllowed;

    private List<Command> subCommands;

    public Command(String aliases, String usage, AccessLevel level, ChannelType... context) {
        this.aliases = Arrays.asList(aliases.split(",,"));
        this.aliases = this.aliases.stream().map(String::toLowerCase).collect(Collectors.toList());

        this.shortDesc = "No description";
        description = shortDesc;

        examples = Arrays.asList(usage.split(",,"));

        guildOnly = new ArrayList<>();
        accessLevel = level;
        hidden = false;
        contextAllowed = Arrays.asList(context);

        subCommands = new ArrayList<>();
    }

    public abstract void execute(MessageReceivedEvent event, String[] args) throws PermissionException;

    protected void registerSubCommands(Command... subs) {
        subCommands.addAll(Arrays.asList(subs));
    }

    protected boolean proceedSubCommand(MessageReceivedEvent event, String[] args) throws PermissionException {
        Command sub = getSubCommand(args[0]);
        if (sub == null)
            return false;

        if (!sub.isAllowedHere(event.getChannelType()))
            return false;

        sub.execute(event, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    protected Command getSubCommand(String alias) {
        alias.toLowerCase();
        for (Command c : subCommands)
            if (c.getAliases().contains(alias))
                return c;
        return null;
    }

    protected void setShortDescription(String s) {
        if (description.equals(shortDesc))
            description = s;
        shortDesc = s;
    }

    public String getName() {
        return aliases.get(0);
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getShortDescription() {
        return shortDesc;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getExemples() {
        return examples;
    }

    public List<String> getGuildOnly() {
        return guildOnly;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public boolean isGuildOnly() {
        return !guildOnly.isEmpty();
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isAllowedHere(ChannelType type) {
        return contextAllowed.contains(type);
    }

    public enum AccessLevel {
        EVERYONE,
        SERVER_OWNER,
        SERVER_OWNER_CREATOR,
        CREATOR
    }
}
