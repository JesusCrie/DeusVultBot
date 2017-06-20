package com.jesus_crie.deusvult.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jesus_crie.deusvult.logger.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Config {

    private static HashMap<String, String> config = new HashMap<>();
    private static HashMap<Integer, Team> teams = new HashMap<>();

    public Config() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            config = mapper.readValue(new URL("http://www.jesus-crie.com/discord/config.json"), new TypeReference<HashMap<String, String>>() {});

            //List<Team> t = mapper.readValue(new URL("http://www.jesus-crie.com/discord/teams.json"), new TypeReference<List<Team>>() {});
            //t.forEach(te -> teams.put(te.getId(), te));
        } catch (IOException e) {
            Logger.error("Can't load config !", e);
        }
    }

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String out = mapper.writeValueAsString(config);
            System.out.println(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSetting(String s) {
        return config.get(s);
    }

    public static String setSetting(String key, String val) {
        return config.put(key, val);
    }

    public static List<Team> getTeams() {
        return new ArrayList<>(teams.values());
    }

    public static Team getTeamById(Integer id) {
        return teams.get(id);
    }

    public static List<Team> getTeamByName(String name) {
        return teams.values()
                .stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public static int getNextTeamId() {
        List<Integer> ks = new ArrayList<>(teams.keySet());
        ks.sort((prev, next) -> prev > next ? 1 : -1);

        return ks.get(ks.size() - 1);
    }

    public static Team saveTeam(Team team) {
        teams.put(team.getId(), team);
        return teams.get(team.getId());
    }
}
