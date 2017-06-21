package com.jesus_crie.deusvult.manager;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.config.Team;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        Guild g = DeusVult.instance().getMainGuild();

        Role role = g.getController()
                .createRole()
                .setPermissions(-1).complete();

        TextChannel chanT = (TextChannel) g.getController()
                .createTextChannel("group-" + name.replaceAll(" ", "_"))
                .addPermissionOverride(g.getPublicRole(), -1, Permission.MESSAGE_READ.getOffset())
                .addPermissionOverride(role, Permission.MESSAGE_READ.getOffset(), -1)
                .complete();

        VoiceChannel chanV = (VoiceChannel) g.getController()
                .createVoiceChannel("\uD83C\uDF0F Groupe - " + name)
                .addPermissionOverride(g.getPublicRole(), -1, Permission.VOICE_CONNECT.getOffset())
                .addPermissionOverride(role, Permission.VOICE_CONNECT.getOffset(), -1)
                .complete();

        Team team = new Team(getNextId(), name, role, owner, chanT, chanV);
        registerTeam(team);

        return team;
    }

    public static void deleteTeam(Team team) {
        teams.remove(team);

        team.getChannelVoice().delete().queue();
        team.getChannelText().delete().queue();
        team.getRole().delete().queue();
    }

    private static int getNextId() {
        teams.sort((prev, next) -> prev.getId() > next.getId() ? 1 : -1);

        return teams.get(teams.size() - 1).getId() + 1;
    }
}
