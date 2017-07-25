package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.config.Team;
import com.jesus_crie.deusvult.manager.TeamManager;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.response.ResponseUtils;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TeamCommand extends Command {

    public TeamCommand() {
        super("team",
                S.COMMAND_TEAM_HELP.get(),
                Collections.singletonList(DeusVult.instance().getMainGuild().getIdLong()),
                AccessLevel.EVERYONE,
                Context.calculate(Context.MAIN_GUILD));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("list")
                }, (e, a) -> onCommandList(e), "list"),

                new CommandPattern(null, (e, a) -> onCommandHelp(e), "")
        );
    }

    private boolean onCommandHelp(MessageReceivedEvent event) { //TODO clear Stirngs
        ResponseBuilder.create(event.getMessage())
                .setTitle(S.COMMAND_TEAM_HELP_HELP.get())
                .setIcon(StringUtils.ICON_CUP)
                .setDescription(S.COMMAND_TEAM_HELP_DESC.get())
                .addField(">team", "Affiche cette aide", false)
                .addField(">team list", "Affiche les 10 plus grosses teams.", false)
                .addField(">team create", "Initialise le créateur de team, les autres étape ce font en MP", false)
                .addField(">team edit", "Reservé au leader, permet d'éditer la team. (renommer, supprimer, inviter/renvoyer des personnes)", false)
                .addField(">team leave", "Reservé aux membres. Permet de quitter une team. Quitter le serveur a pour effet de vous faire quitter toutes vos teams.", false)
                .send(event.getChannel()).queue();

        return true;
    }

    private boolean onCommandList(MessageReceivedEvent event) {
        List<Team> teams = TeamManager.getTeams().stream()
                .sorted((p, n) -> -p.compareTo(n))
                .limit(10)
                .collect(Collectors.toList());

        ResponseBuilder builder = ResponseBuilder.create(event.getMessage())
                .setTitle(S.COMMAND_TEAM_LIST_TITLE.format(teams.size()))
                .setIcon(StringUtils.ICON_CUP);

        if (teams.size() <= 0)
            builder.setDescription(S.COMMAND_TEAM_LIST_NONE.get());
        else
            builder.addField(ResponseUtils.createList("",
                    false,
                    teams.stream()
                            .map(t -> S.COMMAND_TEAM_LIST_PATTERN.format(t.getName(), t.getMembers().size()))
                            .toArray(String[]::new)));

        builder.send(event.getChannel()).queue();
        return true;
    }
}
