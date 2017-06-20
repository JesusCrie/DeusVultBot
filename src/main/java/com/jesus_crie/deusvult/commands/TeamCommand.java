package com.jesus_crie.deusvult.commands;

import com.jesus_crie.deusvult.builder.EmbedMessageBuilder;
import com.jesus_crie.deusvult.config.Config;
import com.jesus_crie.deusvult.config.Team;
import com.jesus_crie.deusvult.utils.F;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.stream.Collectors;

public class TeamCommand extends Command {

    public TeamCommand() {
        super("team,,t",
                "team <create|delete|config|add|list>",
                AccessLevel.CREATOR,
                ChannelType.TEXT);
        guildOnly.add("323949989929812008");
        setShortDescription("Manage teams.");
        description = "Used to create, join, delete and edit your team.";
        registerSubCommands(
                new List()
        );
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) throws PermissionException {
        if (args.length > 0) {
             if (!proceedSubCommand(event, args))
                 event.getChannel().sendMessage(StringUtils.getErrorMessage(event.getAuthor(), "Unknow subcommand.")).queue();
             return;
        }

        EmbedMessageBuilder builder = new EmbedMessageBuilder(event.getAuthor());
        builder.addMainList("Team management", StringUtils.ICON_INFO,
                "Use " + F.code(StringUtils.PREFIX + "team list") + " to see a list of the top 10 teams.",
                "Use " + F.code(StringUtils.PREFIX + "team create") + " to create your own team.",
                "Use " + F.code(StringUtils.PREFIX + "team add") + " to invite someone in your guild.",
                "Use " + F.code(StringUtils.PREFIX + "team config") + " to configure your team.");

        event.getChannel().sendMessage(builder.build()).queue();
    }

    private class List extends Command {

        public List() {
            super("list",
                    "team list",
                    AccessLevel.EVERYONE,
                    ChannelType.TEXT);
            setShortDescription("Show the top of 10 of the teams.");
        }

        @Override
        public void execute(MessageReceivedEvent event, String[] args) throws PermissionException {
            EmbedMessageBuilder builder = new EmbedMessageBuilder(event.getAuthor());

            java.util.List<Team> topTeams = Config.getTeams();
            topTeams.sort((prev, next) -> prev.getMembers().size() > next.getMembers().size() ? 1 : -1);
            topTeams = topTeams.subList(0, Math.min(topTeams.size(), 10));

            builder.addMainList("Team Leaderboard (most members)", StringUtils.ICON_CUP,
                    topTeams.stream()
                            .map(t -> t.getName() + " (" + F.bold(String.valueOf(t.getMembers().size())) + " members)")
                            .collect(Collectors.toList()));

            event.getChannel().sendMessage(builder.build()).queue();
        }
    }
}
