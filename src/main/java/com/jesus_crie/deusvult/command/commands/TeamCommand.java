package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.config.Team;
import com.jesus_crie.deusvult.exception.CommandException;
import com.jesus_crie.deusvult.exception.TimeoutException;
import com.jesus_crie.deusvult.manager.TeamManager;
import com.jesus_crie.deusvult.manager.ThreadManager;
import com.jesus_crie.deusvult.response.DialogBuilder;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.response.ResponseUtils;
import com.jesus_crie.deusvult.utils.StringUtils;
import com.jesus_crie.deusvult.utils.T;
import com.jesus_crie.deusvult.utils.Waiter;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.awt.Color;
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
                        CommandPattern.Argument.forString("leave"),
                        CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommandLeave, "leave <nom de la team>"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("edit"),
                        CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommandEdit, "edit <nom de la team>"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("create")
                }, (e, a) -> onCommandCreate(e), "create"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("list")
                }, (e, a) -> onCommandList(e), "list"),

                new CommandPattern(null, (e, a) -> onCommandHelp(e), "")
        );
    }

    private boolean onCommandHelp(MessageReceivedEvent event) {
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
                .addField(">team create", "Initialise le créateur de team.\n2 teams ne peuvent pas avoir le même non.", false)
                .addField(">team edit <nom de la team>", "Reservé au leader, permet d'éditer la team. (renommer, supprimer, inviter/renvoyer des personnes)", false)
                .addField(">team leave <nom de la team>", "Reservé aux membres. Permet de quitter une team. Quitter le serveur a pour effet de vous faire quitter toutes vos teams.", false)
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
                .setIcon(StringUtils.ICON_CUP)
                .setDescription("Ecrivez le nom de la team. Le nom de doit être consitué uniquement de caractères alphanumériques (lettres sans accents + chiffres) et de _ et -." +
                        "\nLes caractères ne respectant pas cela seront enlevés du nom.")
                .addMention(event.getAuthor());
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

        eventName.getMessage().delete().queue();

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

        eventUsers.getMessage().delete().queue();

        Team team = TeamManager.createTeam(event.getAuthor(), eventName.getMessage().getRawContent());
        if (team == null) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Une team du meme nom éxiste déjà !"))
                    .send(event.getChannel()).queue();
            return true;
        }

        eventUsers.getMessage().getMentionedUsers().forEach(
                u -> ThreadManager.getGeneralPool().execute(() -> TeamManager.sendInvite(u, team)));

        ResponseBuilder.create(event.getMessage())
                .setTitle("La team \"" + team.getName() + "\" a bien été créé !")
                .setColor(Color.GREEN)
                .setIcon(StringUtils.ICON_TERMINAL)
                .setDescription("Des invitations ont été envoyez aux membres.")
                .send(event.getChannel()).queue();

        return true;
    }

    private boolean onCommandEdit(MessageReceivedEvent event, List<Object> args) {
        args.remove(0);
        String query = args.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
        Team result = generateTeamList(event.getAuthor(), true).stream()
                .filter(t -> t.getName().equalsIgnoreCase(query))
                .findAny()
                .orElse(null);

        if (result == null) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'êtes le propriétaire d'aucune team de ce nom."))
                    .send(event.getChannel()).queue();
            return true;
        }

        ResponseBuilder editor = ResponseBuilder.create(event.getMessage())
                .setTitle(f("Editeur de la team %s", result.getName()))
                .setIcon(StringUtils.ICON_CUP)
                .addField("Actions", StringUtils.EMOTE_INCOMING_MESSAGE + " Envoyer une invitation." +
                        "\n\n" + StringUtils.EMOTE_DOOR + " Virer quelqu'un." +
                        "\n\n" + StringUtils.EMOTE_MEMO + " Editer le nom." +
                        "\n\n" + StringUtils.EMOTE_TRASH + " Suprrimer la team.", false)
                .addMention(event.getAuthor());
        Message m = editor.send(event.getChannel()).complete();
        m.addReaction(StringUtils.EMOTE_INCOMING_MESSAGE).complete();
        m.addReaction(StringUtils.EMOTE_DOOR).complete();
        m.addReaction(StringUtils.EMOTE_MEMO).complete();
        m.addReaction(StringUtils.EMOTE_TRASH).complete();

        MessageReactionAddEvent eventEdit = Waiter.getNextEvent(MessageReactionAddEvent.class,
                e -> e.getMessageIdLong() == m.getIdLong() && e.getUser().equals(event.getAuthor())
                        && (e.getReactionEmote().getName().equals(StringUtils.EMOTE_INCOMING_MESSAGE)
                            || e.getReactionEmote().getName().equals(StringUtils.EMOTE_DOOR)
                            || e.getReactionEmote().getName().equals(StringUtils.EMOTE_MEMO)
                            || e.getReactionEmote().getName().equals(StringUtils.EMOTE_TRASH)),
                () -> {
                    m.clearReactions().complete();
                    ResponseUtils.errorMessage(event.getMessage(), new TimeoutException("Vous avez mis trop longtemps à répondre."))
                            .send(event.getChannel()).complete();
                }, T.calc(30));
        if (eventEdit == null)
            return true;

        m.clearReactions().queue();

        switch (eventEdit.getReactionEmote().getName()) {
            case StringUtils.EMOTE_INCOMING_MESSAGE:
                return onEditInvite(eventEdit, result, editor);
            case StringUtils.EMOTE_DOOR:
                return onEditFired(eventEdit, result, editor);
            case StringUtils.EMOTE_MEMO:
                return onEditRename(eventEdit, result, editor);
            case StringUtils.EMOTE_TRASH:
                return onEditDelete(eventEdit, result, editor);
            default:
                ResponseUtils.errorMessage(event.getMessage(), new CommandException("J'ai aucune idée de comment ceci vient d'arriver."))
                        .send(event.getChannel());
                break;
        }
        return true;
    }

    private boolean onEditInvite(MessageReactionAddEvent event, Team team, ResponseBuilder editor) {
        editor.setDescription("Mentionnez les personnes que vous voulez inviter.")
                .clearLists()
                .send(event.getChannel()).complete();

        MessageReceivedEvent eventInvite = Waiter.getNextMessageFromUser(event.getChannel(), event.getUser(),
                () -> ResponseUtils.errorMessage(event.getChannel().getMessageById(event.getMessageIdLong()).complete(),
                            new TimeoutException("Vous avez mis trop de temps à répondre."))
                            .send(event.getChannel()).complete(),
                T.calc(1, TimeUnit.MINUTES));

        if (eventInvite == null)
            return true;

        List<User> toInvite = eventInvite.getMessage().getMentionedUsers();
        if (toInvite.size() <= 0) {
            ResponseUtils.errorMessage(eventInvite.getMessage(), new CommandException("Vous n'avez mentionner personne !"))
                    .send(eventInvite.getChannel()).complete();
            return true;
        }

        toInvite.forEach(u -> ThreadManager.getGeneralPool().execute(() -> TeamManager.sendInvite(u, team)));

        ResponseBuilder.create(eventInvite.getMessage())
                .setTitle("Les invitations ont bien été envoyés !")
                .setIcon(StringUtils.ICON_CUP)
                .setColor(Color.GREEN)
                .addMention(eventInvite.getAuthor())
                .send(event.getChannel()).complete();
        return true;
    }

    private boolean onEditFired(MessageReactionAddEvent event, Team team, ResponseBuilder editor) {
        editor.setDescription("Mentionnez les personnes que vous voulez virez.")
                .clearLists()
                .send(event.getChannel()).complete();

        MessageReceivedEvent eventFire = Waiter.getNextMessageFromUser(event.getChannel(), event.getUser(),
                () -> ResponseUtils.errorMessage(event.getChannel().getMessageById(event.getMessageIdLong()).complete(),
                        new TimeoutException("Vous avez mis trop de temps à répondre."))
                        .send(event.getChannel()).complete(),
                T.calc(1, TimeUnit.MINUTES));

        if (eventFire == null)
            return true;

        List<User> toFire = eventFire.getMessage().getMentionedUsers();
        if (toFire.size() <= 0) {
            ResponseUtils.errorMessage(eventFire.getMessage(), new CommandException("Vous n'avez mentionner personne !"))
                    .send(eventFire.getChannel()).complete();
            return true;
        }

        toFire.forEach(team::removeMember);

        ResponseBuilder.create(eventFire.getMessage())
                .setTitle("Les membres ont bien été virés !")
                .setIcon(StringUtils.ICON_CUP)
                .setColor(Color.GREEN)
                .addMention(eventFire.getAuthor())
                .send(event.getChannel()).complete();
        return true;
    }

    private boolean onEditRename(MessageReactionAddEvent event, Team team, ResponseBuilder editor) {
        editor.setDescription("Tapez le nouveau nom de votre team.")
                .clearLists()
                .send(event.getChannel()).complete();

        MessageReceivedEvent eventName = Waiter.getNextMessageFromUser(event.getChannel(), event.getUser(),
                () -> ResponseUtils.errorMessage(event.getChannel().getMessageById(event.getMessageIdLong()).complete(),
                        new TimeoutException("Vous avez mis trop de temps à répondre."))
                        .send(event.getChannel()).complete(),
                T.calc(1, TimeUnit.MINUTES));

        if (eventName == null)
            return true;

        String newName = eventName.getMessage().getRawContent();
        if (TeamManager.getTeamByName(newName) != null) {
            ResponseUtils.errorMessage(eventName.getMessage(), new CommandException("Une team du même nom existe déjà !"))
                    .send(eventName.getChannel()).complete();
            return true;
        }

        if (team.rename(newName))
            ResponseBuilder.create(eventName.getMessage())
                    .setTitle(f("La team \"%s\" à bien été rennomée !", team.getName()))
                    .setIcon(StringUtils.ICON_CUP)
                    .setColor(Color.GREEN)
                    .send(eventName.getChannel()).complete();
        else
            ResponseUtils.errorMessage(eventName.getMessage(), new CommandException("Ce nom n'est pas un nom valide !"))
                    .send(eventName.getChannel()).complete();

        return true;
    }

    private boolean onEditDelete(MessageReactionAddEvent event, Team team, ResponseBuilder editor) {
        boolean confirm = new DialogBuilder(team.getOwner())
                .setContent("Etes vous sur de vouloir faire ceci ?")
                .send(event.getChannel());

        if (confirm) {
            editor.clearLists()
                    .setDescription("Team en cours de suppression...")
                    .send(event.getChannel()).complete();
            TeamManager.deleteTeam(team);
        }
        return true;
    }

    private boolean onCommandLeave(MessageReceivedEvent event, List<Object> args) {
        args.remove(0);
        String query = args.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
        Team result = generateTeamList(event.getAuthor(), false).stream()
                .filter(t -> t.getName().equalsIgnoreCase(query))
                .findAny()
                .orElse(null);

        if (result == null) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'êtes membre d'aucune team de ce nom."))
                    .send(event.getChannel()).queue();
            return true;
        }

        result.removeMember(event.getAuthor());

        ResponseBuilder.create(event.getMessage())
                .setTitle(f("Vous avez bien quitter la team %s", result.getName()))
                .setIcon(StringUtils.ICON_CUP)
                .send(event.getChannel()).queue();

        return true;
    }

    private List<Team> generateTeamList(User u, boolean isOwner) {
        final List<Team> teams;
        if (isOwner)
            teams = TeamManager.getTeamsOwnedForUser(u);
        else {
            teams = TeamManager.getTeamsForUser(u).stream()
                    .filter(t -> !t.isOwner(u))
                    .collect(Collectors.toList());
        }

        return teams;
    }
}
