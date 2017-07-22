package com.jesus_crie.deusvult.manager;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.config.Config;
import com.jesus_crie.deusvult.config.Team;
import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.utils.S;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.restaction.order.ChannelOrderAction;

import java.util.*;
import java.util.stream.Collectors;

public class TeamManager {

    private static List<Team> teams = new ArrayList<>();

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
                    .get();
        } catch (NullPointerException e) {
            return null;
        }
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
        Logger.TEAM.get().info("Creating team " + name);
        Guild g = DeusVult.instance().getMainGuild();

        Role role = g.getController()
                .createRole()
                .setName("Team - " + name)
                .complete();
        g.getController().addRolesToMember(g.getMember(owner), role).complete();

        TextChannel chanT = (TextChannel) g.getController()
                .createTextChannel(S.TEAM_CHANNEL_TEXT_NAME.format(name.replaceAll(" ", "_")))
                .addPermissionOverride(g.getPublicRole(), new ArrayList<>(), Collections.singletonList(Permission.MESSAGE_READ))
                .addPermissionOverride(role, Collections.singletonList(Permission.MESSAGE_READ), new ArrayList<>())
                .addPermissionOverride(g.getRoleById("323952614892896261"), Collections.singletonList(Permission.MESSAGE_READ), new ArrayList<>())
                .setTopic(S.TEAM_CHANNEL_TEXT_TOPIC.format(name))
                .complete();

        VoiceChannel chanV = (VoiceChannel) g.getController()
                .createVoiceChannel(S.TEAM_CHANNEL_VOICE_NAME.format(name))
                .addPermissionOverride(g.getPublicRole(), new ArrayList<>(), Collections.singletonList(Permission.VOICE_CONNECT))
                .addPermissionOverride(role, Collections.singletonList(Permission.VOICE_CONNECT), new ArrayList<>())
                .addPermissionOverride(g.getRoleById("323952614892896261"), Collections.singletonList(Permission.VOICE_CONNECT), new ArrayList<>())
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
        teams.sort((prev, next) -> prev.getId() > next.getId() ? 1 : -1);

        return teams.get(teams.size() - 1).getId() + 1;
    }
}