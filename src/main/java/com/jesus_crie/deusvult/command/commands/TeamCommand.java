package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.config.Team;
import com.jesus_crie.deusvult.exception.CommandException;
import com.jesus_crie.deusvult.exception.TimeoutException;
import com.jesus_crie.deusvult.manager.TeamManager;
import com.jesus_crie.deusvult.manager.ThreadManager;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.response.ResponsePage;
import com.jesus_crie.deusvult.response.ResponseUtils;
import com.jesus_crie.deusvult.utils.StringUtils;
import com.jesus_crie.deusvult.utils.T;
import com.jesus_crie.deusvult.utils.Waiter;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jesus_crie.deusvult.utils.S.*;

public class TeamCommand extends Command {

    public TeamCommand() {
        super("team",
                "Permet de faire toutes les actions possible avec les teams.",
                Collections.singletonList(DeusVult.instance().getMainGuild().getIdLong()),
                AccessLevel.EVERYONE,
                Context.calculate(Context.MAIN_GUILD));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("leave")
                }, (e, a) -> onCommandLeave(e), "leave"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("edit")
                }, (e, a) -> onCommandEdit(e), "edit"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("create")
                }, (e, a) -> onCommandCreate(e), "create"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("list")
                }, (e, a) -> onCommandList(e), "list"),

                new CommandPattern(null, (e, a) -> onCommandHelp(e), "")
        );
    }

    private boolean onCommandHelp(MessageReceivedEvent event) { //TODO clear Strings
        ResponseBuilder.create(event.getMessage())
                .setTitle("Aide sur les teams")
                .setIcon(StringUtils.ICON_CUP)
                .setDescription("Une team est un groupe de joueurs (minimum 2) ayant un channel textuel et vocal dédié sur ce serveur." +
                        "\nLe leader de la team peut inviter/exclure des membres, renommer la team, la supprimer et donner son titre à un autre membre. Il dispose également d'un rang exclusif." +
                        "\nLes membres de la team peuvent uniquement la quitter et possedent un rang indiquant le nom de la team." +
                        "\nQuand une team atteint 1 membre elle est automatiquement supprimée." +
                        "\nA cause du système de permissions de Discord les admins (uniquement) ont accès aux channels de team.")
                .addField(">team", "Affiche cette aide", false)
                .addField(">team list", "Affiche les 10 plus grosses teams.", false)
                .addField(">team create", "Initialise le créateur de team.", false)
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
                .setTitle(f("Top 10 des plus grosses teams (Total: %s)", teams.size()))
                .setIcon(StringUtils.ICON_CUP);

        if (teams.size() <= 0)
            builder.setDescription("Aucune team n'a été trouver !");
        else
            builder.addField(ResponseUtils.createList("",
                    false,
                    teams.stream()
                            .map(t -> f("%s (%s)", t.getName(), t.getMembers().size()))
                            .toArray(String[]::new)));

        builder.send(event.getChannel()).queue();
        return true;
    }

    private boolean onCommandCreate(MessageReceivedEvent event) {
        ResponseBuilder builder = ResponseBuilder.create(event.getMessage())
                .setTitle("Création de Team")
                .setIcon(StringUtils.ICON_TERMINAL)
                .setDescription("Ecrivez le nom de la team.");
        builder.send(event.getChannel()).complete();

        MessageReceivedEvent eventName = Waiter.getNextMessageFromUser(event.getChannel(), event.getAuthor(),
                () -> ResponseUtils.errorMessage(event.getMessage(),
                        new TimeoutException(event.getAuthor().getAsMention() + ", vous avez mis trop longtemps a répondre !"))
                        .send(event.getChannel()).queue(),
                T.calc(1, TimeUnit.MINUTES));

        if (eventName == null)
            return true;
        if (eventName.getMessage().getRawContent().isEmpty()) {
            ResponseUtils.errorMessage(eventName.getMessage(), new CommandException("Nom invalide !")).send(eventName.getChannel()).queue();
            return true;
        }

        builder.setDescription("Mentionnez toutes les personnes que vous voulez inviter.")
                .send(event.getChannel()).complete();

        MessageReceivedEvent eventUsers = Waiter.getNextMessageFromUser(event.getChannel(), event.getAuthor(),
                () -> ResponseUtils.errorMessage(event.getMessage(),
                        new TimeoutException(event.getAuthor().getAsMention() + ", vous avez mis trop longtemps a répondre !"))
                        .send(event.getChannel()).queue(),
                T.calc(1, TimeUnit.MINUTES));

        if (eventUsers == null)
            return true;
        if (eventUsers.getMessage().getMentionedUsers().isEmpty()) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous devez mentionner au moins 1 personne !")).send(eventUsers.getChannel()).queue();
            return true;
        }

        Team team = TeamManager.createTeam(event.getAuthor(), eventName.getMessage().getRawContent());

        eventUsers.getMessage().getMentionedUsers().forEach(
                u -> ThreadManager.getGeneralPool().execute(() -> TeamManager.sendInvite(u, team)));

        ResponseBuilder.create(event.getMessage())
                .setTitle("La team \"" + team.getName() + "\" a bien été créé !")
                .setIcon(StringUtils.ICON_TERMINAL)
                .setDescription("Des invitations ont été envoyez aux membres.")
                .send(event.getChannel()).queue();

        return true;
    }

    private boolean onCommandEdit(MessageReceivedEvent event) {
        return true;
    }

    private boolean onCommandLeave(MessageReceivedEvent event) {
        return true;
    }

    private List<ResponsePage> generateTeamList(User u, boolean isOwner) {
        final List<Team> teams;
        if (isOwner)
            teams = TeamManager.getTeamsOwnedForUser(u);
        else {
            teams = TeamManager.getTeamsForUser(u).stream()
                    .filter(t -> !t.isOwner(u))
                    .collect(Collectors.toList());
        }

        return teams.stream()
                .map(team ->
                    new ResponsePage(null)
                        .addFields(ResponseUtils.createList(team.getName(), false,
                                "Owner: " + team.getOwner().getAsMention(),
                                "Membres: " + team.getMembers().size())))
                .collect(Collectors.toList());
    }
}
