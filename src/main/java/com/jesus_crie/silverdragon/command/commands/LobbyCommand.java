package com.jesus_crie.silverdragon.command.commands;

import com.jesus_crie.silverdragon.SilverDragon;
import com.jesus_crie.silverdragon.command.Command;
import com.jesus_crie.silverdragon.command.CommandPattern;
import com.jesus_crie.silverdragon.config.Lobby;
import com.jesus_crie.silverdragon.exception.CommandException;
import com.jesus_crie.silverdragon.exception.TimeoutException;
import com.jesus_crie.silverdragon.manager.LobbyManager;
import com.jesus_crie.silverdragon.manager.ThreadManager;
import com.jesus_crie.silverdragon.response.DialogBuilder;
import com.jesus_crie.silverdragon.response.ResponseBuilder;
import com.jesus_crie.silverdragon.response.ResponseUtils;
import com.jesus_crie.silverdragon.utils.StringUtils;
import com.jesus_crie.silverdragon.utils.T;
import com.jesus_crie.silverdragon.utils.Waiter;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jesus_crie.silverdragon.utils.S.f;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby",
                "Permet de faire toutes les actions possible avec les lobby.",
                Collections.singletonList(SilverDragon.instance().getMainGuild().getIdLong()),
                AccessLevel.EVERYONE,
                Context.calculate(Context.MAIN_GUILD));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("leave"),
                        CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommandLeave, "leave <nom du lobby>"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("edit"),
                        CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommandEdit, "edit <nom du lobby>"),

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
                .setDescription("") //TODO
                .addField(">lobby", "Affiche cette aide", false)
                .addField(">lobby list", "Affiche les 10 plus gros lobby.", false)
                .addField(">lobby create", "Initialise le créateur du lobby.\n2 lobby ne peuvent pas avoir le même non.", false)
                .addField(">lobby edit <nom du lobby>", "Reservé au leader, permet d'éditer le lobby. (renommer, supprimer, inviter/renvoyer des personnes)", false)
                .addField(">lobby leave <nom du lobby>", "Reservé aux membres. Permet de quitter un lobby. Quitter le serveur a pour effet de vous faire quitter toutes vos lobby.", false)
                .send(event.getChannel()).queue();

        return true;
    }

    private boolean onCommandList(MessageReceivedEvent event) {
        List<Lobby> lobbies = LobbyManager.getTeams().stream()
                .sorted((p, n) -> -p.compareTo(n))
                .limit(10)
                .collect(Collectors.toList());

        ResponseBuilder builder = ResponseBuilder.create(event.getMessage())
                .setTitle(f("Top 10 des plus gros lobby (Total: %s)", lobbies.size()))
                .setIcon(StringUtils.ICON_CUP);

        if (lobbies.size() <= 0)
            builder.setDescription("Aucun lobby n'a été trouver !");
        else
            builder.addField(ResponseUtils.createList("",
                    false,
                    lobbies.stream()
                            .map(t -> f("%s (%s)", t.getName(), t.getMembers().size()))
                            .toArray(String[]::new)));

        builder.send(event.getChannel()).queue();
        return true;
    }

    private boolean onCommandCreate(MessageReceivedEvent event) {
        ResponseBuilder builder = ResponseBuilder.create(event.getMessage())
                .setTitle("Création de Lobby")
                .setIcon(StringUtils.ICON_CUP)
                .setDescription("Ecrivez le nom du lobby. Le nom de doit être consitué uniquement de caractères alphanumériques (lettres sans accents + chiffres) et de _ et -." +
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

        Lobby lobby = LobbyManager.createTeam(event.getAuthor(), eventName.getMessage().getRawContent());
        if (lobby == null) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Un lobby du meme nom éxiste déjà !"))
                    .send(event.getChannel()).queue();
            return true;
        }

        eventUsers.getMessage().getMentionedUsers().forEach(
                u -> ThreadManager.getGeneralPool().execute(() -> LobbyManager.sendInvite(u, lobby)));

        ResponseBuilder.create(event.getMessage())
                .setTitle("Le lobby \"" + lobby.getName() + "\" a bien été créé !")
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
        Lobby result = generateTeamList(event.getAuthor(), true).stream()
                .filter(t -> t.getName().equalsIgnoreCase(query))
                .findAny()
                .orElse(null);

        if (result == null) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'êtes le propriétaire d'aucun lobby de ce nom."))
                    .send(event.getChannel()).queue();
            return true;
        }

        ResponseBuilder editor = ResponseBuilder.create(event.getMessage())
                .setTitle(f("Editeur du lobby %s", result.getName()))
                .setIcon(StringUtils.ICON_CUP)
                .addField("Actions", StringUtils.EMOTE_INCOMING_MESSAGE + " Envoyer une invitation." +
                        "\n\n" + StringUtils.EMOTE_DOOR + " Virer quelqu'un." +
                        "\n\n" + StringUtils.EMOTE_MEMO + " Editer le nom." +
                        "\n\n" + StringUtils.EMOTE_TRASH + " Suprrimer le lobby.", false)
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

    private boolean onEditInvite(MessageReactionAddEvent event, Lobby lobby, ResponseBuilder editor) {
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

        toInvite.forEach(u -> ThreadManager.getGeneralPool().execute(() -> LobbyManager.sendInvite(u, lobby)));

        ResponseBuilder.create(eventInvite.getMessage())
                .setTitle("Les invitations ont bien été envoyés !")
                .setIcon(StringUtils.ICON_CUP)
                .setColor(Color.GREEN)
                .addMention(eventInvite.getAuthor())
                .send(event.getChannel()).complete();
        return true;
    }

    private boolean onEditFired(MessageReactionAddEvent event, Lobby lobby, ResponseBuilder editor) {
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

        toFire.forEach(lobby::removeMember);

        ResponseBuilder.create(eventFire.getMessage())
                .setTitle("Les membres ont bien été virés !")
                .setIcon(StringUtils.ICON_CUP)
                .setColor(Color.GREEN)
                .addMention(eventFire.getAuthor())
                .send(event.getChannel()).complete();
        return true;
    }

    private boolean onEditRename(MessageReactionAddEvent event, Lobby lobby, ResponseBuilder editor) {
        editor.setDescription("Tapez le nouveau nom de votre lobby.")
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
        if (LobbyManager.getTeamByName(newName) != null) {
            ResponseUtils.errorMessage(eventName.getMessage(), new CommandException("Un lobby du même nom existe déjà !"))
                    .send(eventName.getChannel()).complete();
            return true;
        }

        if (lobby.rename(newName))
            ResponseBuilder.create(eventName.getMessage())
                    .setTitle(f("Le lobby \"%s\" à bien été rennomée !", lobby.getName()))
                    .setIcon(StringUtils.ICON_CUP)
                    .setColor(Color.GREEN)
                    .send(eventName.getChannel()).complete();
        else
            ResponseUtils.errorMessage(eventName.getMessage(), new CommandException("Ce nom n'est pas un nom valide !"))
                    .send(eventName.getChannel()).complete();

        return true;
    }

    private boolean onEditDelete(MessageReactionAddEvent event, Lobby lobby, ResponseBuilder editor) {
        boolean confirm = new DialogBuilder(lobby.getOwner())
                .setContent("Etes vous sur de vouloir faire ceci ?")
                .send(event.getChannel());

        if (confirm) {
            editor.clearLists()
                    .setDescription("Lobby en cours de suppression...")
                    .send(event.getChannel()).complete();
            LobbyManager.deleteTeam(lobby);
        }
        return true;
    }

    private boolean onCommandLeave(MessageReceivedEvent event, List<Object> args) {
        args.remove(0);
        String query = args.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "));
        Lobby result = generateTeamList(event.getAuthor(), false).stream()
                .filter(t -> t.getName().equalsIgnoreCase(query))
                .findAny()
                .orElse(null);

        if (result == null) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'êtes membre d'aucun lobby de ce nom."))
                    .send(event.getChannel()).queue();
            return true;
        }

        result.removeMember(event.getAuthor());

        ResponseBuilder.create(event.getMessage())
                .setTitle(f("Vous avez bien quitter le lobby %s", result.getName()))
                .setIcon(StringUtils.ICON_CUP)
                .send(event.getChannel()).queue();

        return true;
    }

    private List<Lobby> generateTeamList(User u, boolean isOwner) {
        final List<Lobby> lobbies;
        if (isOwner)
            lobbies = LobbyManager.getTeamsOwnedForUser(u);
        else {
            lobbies = LobbyManager.getTeamsForUser(u).stream()
                    .filter(t -> !t.isOwner(u))
                    .collect(Collectors.toList());
        }

        return lobbies;
    }
}
