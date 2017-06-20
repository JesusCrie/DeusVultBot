package com.jesus_crie.deusvult.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.utils.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Config {

    private static HashMap<String, String> config = new HashMap<>();
    private static HashMap<Integer, Team> teams = new HashMap<>();
    private static String secret;

    public Config(String secret) {
        Config.secret = secret;
        try {
            ObjectMapper mapper = new ObjectMapper();
            config = mapper.readValue(new URL(StringUtils.CONFIG_URL_GENERAL), new TypeReference<HashMap<String, String>>() {});

            //List<Team> t = mapper.readValue(new URL(StringUtils.CONFIG_URL_TEAMS), new TypeReference<List<Team>>() {});
            //t.forEach(te -> teams.put(te.getId(), te));
        } catch (IOException e) {
            Logger.error("Can't load config !", e);
        }
    }

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String outCfg = mapper.writeValueAsString(config);
            String outTeam = mapper.writeValueAsString(teams);

            // Send post request
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(StringUtils.CONFIG_URL_SAVE);

            post.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                    new BasicNameValuePair("_secret", secret),
                    new BasicNameValuePair("config", outCfg),
                    new BasicNameValuePair("teams", outTeam)
            )));

            client.execute(post, response -> {
                JsonNode res = mapper.readValue(response.getEntity().getContent(), JsonNode.class);
                switch (res.get("code").asInt()) {
                    case 1:
                        throw new IOException("Wrong method !");
                    case 2:
                        throw new IOException("Missing datas !");
                    case 0:
                    default:
                        Logger.info("[Config] Successfully saved !");
                }

                return response;
            });
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
