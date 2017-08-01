package com.jesus_crie.silverdragon.manager;

import com.jesus_crie.silverdragon.SilverDragon;
import com.jesus_crie.silverdragon.config.Config;
import com.jesus_crie.silverdragon.config.Lobby;
import com.jesus_crie.silverdragon.logger.Logger;
import com.jesus_crie.silverdragon.response.DialogBuilder;
import com.jesus_crie.silverdragon.utils.StringUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.restaction.order.ChannelOrderAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.jesus_crie.silverdragon.utils.S.f;

public class LobbyManager {

    private static final List<Lobby> teams = new ArrayList<>();

    public static void registerTeam(Lobby lobby) {
        teams.add(lobby);
    }

    public static void registerTeams(List<Lobby> lobbies) {
        LobbyManager.teams.addAll(lobbies);
    }

    public static List<Lobby> getTeams() {
        return teams;
    }

    public static Lobby getTeamById(int id) {
        try {
            return teams.stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .orElse(null);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static Lobby getTeamByName(String name) {
        return teams.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static List<Lobby> getTeamsForUser(User u) {
        return teams.stream()
                .filter(t -> t.isMember(u) || t.isOwner(u))
                .collect(Collectors.toList());
    }

    public static List<Lobby> getTeamsOwnedForUser(User u) {
        return teams.stream()
                .filter(t -> t.isOwner(u))
                .collect(Collectors.toList());
    }

    public static Lobby createTeam(User owner, String name) {
        name = name.trim().replaceAll("[^a-zA-Z0-9 _-]", "");

        if (getTeamByName(name) != null) {
            Logger.TEAM.get().warning(f("Lobby %s already exist !", name));
            return null;
        }

        Logger.TEAM.get().info(f("Creating lobby %s", name));
        Guild g = SilverDragon.instance().getMainGuild();

        Role role = g.getController()
                .createRole()
                .setName(f("Lobby - %s", name))
                .complete();
        g.getController().addRolesToMember(g.getMember(owner), role).complete();

        TextChannel chanT = (TextChannel) g.getController()
                .createTextChannel(f("lobby-%s", name.replaceAll(" ", "_")))
                .addPermissionOverride(g.getPublicRole(), new ArrayList<>(), Collections.singletonList(Permission.MESSAGE_READ))
                .addPermissionOverride(role, Collections.singletonList(Permission.MESSAGE_READ), new ArrayList<>())
                .addPermissionOverride(g.getRoleById(StringUtils.ROLE_BOT), Collections.singletonList(Permission.MESSAGE_READ), new ArrayList<>())
                .setTopic(f("Channel de la lobby %s", name))
                .complete();

        VoiceChannel chanV = (VoiceChannel) g.getController()
                .createVoiceChannel(f("\uD83C\uDF0F Lobby - %s", name))
                .addPermissionOverride(g.getPublicRole(), new ArrayList<>(), Collections.singletonList(Permission.VOICE_CONNECT))
                .addPermissionOverride(role, Collections.singletonList(Permission.VOICE_CONNECT), new ArrayList<>())
                .addPermissionOverride(g.getRoleById(StringUtils.ROLE_BOT), Collections.singletonList(Permission.VOICE_CONNECT), new ArrayList<>())
                .setUserlimit(10)
                .complete();

        Lobby lobby = new Lobby(getNextId(), name, role, owner, chanT, chanV);
        registerTeam(lobby);
        sortChannels();

        return lobby;
    }

    public static void deleteTeam(Lobby lobby) {
        Logger.TEAM.get().info("Deleting lobby " + lobby.getName());

        lobby.delete();

        teams.remove(lobby);
    }

    public static void sendInvite(User toInvite, Lobby lobby) {
        if (lobby.isMember(toInvite) || lobby.isOwner(toInvite) || toInvite.isBot() || toInvite.isFake() || toInvite.equals(SilverDragon.instance().getJDA().getSelfUser()))
            return;

        DialogBuilder dialog = new DialogBuilder(toInvite);
        dialog.setContent("Vous avez recu une invitation pour rejoindre \"" + lobby.getName() + "\"");

        if (dialog.send(toInvite.openPrivateChannel().complete()))
            lobby.addMember(toInvite);
    }

    public static void sortChannels() {
        Guild g = SilverDragon.instance().getMainGuild();

        // Text Channels
        ChannelOrderAction<TextChannel> reorderingT = g.getController().modifyTextChannelPositions();
        int offsetT = reorderingT.selectPosition(g.getTextChannelById(Config.getSetting("teamChannelTAfterId"))).getSelectedPosition();

        List<TextChannel> teamChannelsT = LobbyManager.getTeams().stream()
                .map(Lobby::getChannelText)
                .sorted(Comparator.comparing(Channel::getName))
                .collect(Collectors.toList());

        teamChannelsT.forEach(c -> {
            reorderingT.selectPosition(c);
            reorderingT.moveTo(teamChannelsT.indexOf(c) + offsetT + 1);
        });

        // Voice Channels
        ChannelOrderAction<VoiceChannel> reorderingV = g.getController().modifyVoiceChannelPositions();
        int offsetV = reorderingV.selectPosition(g.getVoiceChannelById(Config.getSetting("teamChannelVAfterId"))).getSelectedPosition();

        List<VoiceChannel> teamChannelsV = LobbyManager.getTeams().stream()
                .map(Lobby::getChannelVoice)
                .sorted(Comparator.comparing(Channel::getName))
                .collect(Collectors.toList());

        teamChannelsV.forEach(c -> {
            reorderingV.selectPosition(c);
            reorderingV.moveTo(teamChannelsV.indexOf(c) + offsetV + 1);
        });

        reorderingT.queue();
        reorderingV.queue();
    }

    private static int getNextId() {
        if (teams.isEmpty())
            return 0;

        teams.sort((prev, next) -> prev.getId() > next.getId() ? 1 : -1);

        return teams.get(teams.size() - 1).getId() + 1;
    }

    public static void cleanupTeams() {
        List<Lobby> clean = new ArrayList<>();

        for (int i = 0; i < teams.size(); i++) {
            Lobby t = teams.get(i);
            if (t.getId() != i)
                t = new Lobby(i, t.getName(), t.getRole(), t.getOwner(), t.getChannelText(), t.getChannelVoice());
            clean.add(t);
        }

        teams.clear();
        teams.addAll(clean);
    }
}