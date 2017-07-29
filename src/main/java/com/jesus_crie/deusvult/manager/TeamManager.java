package com.jesus_crie.deusvult.manager;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.config.Config;
import com.jesus_crie.deusvult.config.Team;
import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.response.DialogBuilder;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.restaction.order.ChannelOrderAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.jesus_crie.deusvult.utils.S.*;

public class TeamManager {

    private static final List<Team> teams = new ArrayList<>();

    public static void registerTeam(Team team) {
        teams.add(team);
    }

    public static void registerTeams(List<Team> teams) {
        TeamManager.teams.addAll(teams);
    }

    public static List<Team> getTeams() {
        return teams;
    }

    public static Team getTeamById(int id) {
        try {
            return teams.stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .orElse(null);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static Team getTeamByName(String name) {
        return teams.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static List<Team> getTeamsForUser(User u) {
        return teams.stream()
                .filter(t -> t.isMember(u) || t.isOwner(u))
                .collect(Collectors.toList());
    }

    public static List<Team> getTeamsOwnedForUser(User u) {
        return teams.stream()
                .filter(t -> t.isOwner(u))
                .collect(Collectors.toList());
    }

    public static Team createTeam(User owner, String name) {
        name = name.replaceAll("[^a-zA-Z0-9 _-]", "");

        if (getTeamByName(name) != null) {
            Logger.TEAM.get().warning(f("Team %s already exist !", name));
            return null;
        }

        Logger.TEAM.get().info(f("Creating team %s", name));
        Guild g = DeusVult.instance().getMainGuild();

        Role role = g.getController()
                .createRole()
                .setName(f("Team - %s", name))
                .complete();
        g.getController().addRolesToMember(g.getMember(owner), role).complete();

        TextChannel chanT = (TextChannel) g.getController()
                .createTextChannel(f("team-%s", name.replaceAll(" ", "_")))
                .addPermissionOverride(g.getPublicRole(), new ArrayList<>(), Collections.singletonList(Permission.MESSAGE_READ))
                .addPermissionOverride(role, Collections.singletonList(Permission.MESSAGE_READ), new ArrayList<>())
                .addPermissionOverride(g.getRoleById(StringUtils.ROLE_BOT), Collections.singletonList(Permission.MESSAGE_READ), new ArrayList<>())
                .setTopic(f("Channel de la team %s", name))
                .complete();

        VoiceChannel chanV = (VoiceChannel) g.getController()
                .createVoiceChannel(f("\uD83C\uDF0F Team - %s", name))
                .addPermissionOverride(g.getPublicRole(), new ArrayList<>(), Collections.singletonList(Permission.VOICE_CONNECT))
                .addPermissionOverride(role, Collections.singletonList(Permission.VOICE_CONNECT), new ArrayList<>())
                .addPermissionOverride(g.getRoleById(StringUtils.ROLE_BOT), Collections.singletonList(Permission.VOICE_CONNECT), new ArrayList<>())
                .setUserlimit(10)
                .complete();

        Team team = new Team(getNextId(), name, role, owner, chanT, chanV);
        registerTeam(team);
        sortChannels();

        return team;
    }

    public static void deleteTeam(Team team) {
        Logger.TEAM.get().info("Deleting team " + team.getName());

        team.delete();

        teams.remove(team);
    }

    public static void sendInvite(User toInvite, Team team) {
        if (team.isMember(toInvite) || team.isOwner(toInvite) || toInvite.isBot() || toInvite.isFake() || toInvite.equals(DeusVult.instance().getJDA().getSelfUser()))
            return;

        DialogBuilder dialog = new DialogBuilder(toInvite);
        dialog.setContent("Vous avez recu une invitation pour rejoindre \"" + team.getName() + "\"");

        if (dialog.send(toInvite.openPrivateChannel().complete()))
            team.addMember(toInvite);
    }

    public static void sortChannels() {
        Guild g = DeusVult.instance().getMainGuild();

        // Text Channels
        ChannelOrderAction<TextChannel> reorderingT = g.getController().modifyTextChannelPositions();
        int offsetT = reorderingT.selectPosition(g.getTextChannelById(Config.getSetting("teamChannelTAfterId"))).getSelectedPosition();

        List<TextChannel> teamChannelsT = TeamManager.getTeams().stream()
                .map(Team::getChannelText)
                .sorted(Comparator.comparing(Channel::getName))
                .collect(Collectors.toList());

        teamChannelsT.forEach(c -> {
            reorderingT.selectPosition(c);
            reorderingT.moveTo(teamChannelsT.indexOf(c) + offsetT + 1);
        });

        // Voice Channels
        ChannelOrderAction<VoiceChannel> reorderingV = g.getController().modifyVoiceChannelPositions();
        int offsetV = reorderingV.selectPosition(g.getVoiceChannelById(Config.getSetting("teamChannelVAfterId"))).getSelectedPosition();

        List<VoiceChannel> teamChannelsV = TeamManager.getTeams().stream()
                .map(Team::getChannelVoice)
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
        List<Team> clean = new ArrayList<>();

        for (int i = 0; i < teams.size(); i++) {
            Team t = teams.get(i);
            if (t.getId() != i)
                t = new Team(i, t.getName(), t.getRole(), t.getOwner(), t.getChannelText(), t.getChannelVoice());
            clean.add(t);
        }

        teams.clear();
        teams.addAll(clean);
    }
}