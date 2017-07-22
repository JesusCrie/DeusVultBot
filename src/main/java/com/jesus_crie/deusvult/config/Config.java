package com.jesus_crie.deusvult.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesus_crie.deusvult.exception.ConfigException;
import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.manager.TeamManager;
import com.jesus_crie.deusvult.utils.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Config {

    private static HashMap<String, String> config = new HashMap<>();
    private static String secret;

    public Config(String secret) {
        Config.secret = secret;
        try {
            ObjectMapper mapper = new ObjectMapper();
            config = mapper.readValue(new URL(StringUtils.CONFIG_URL_GENERAL), new TypeReference<HashMap<String, String>>() {});

            List<Team> t = mapper.readValue(new URL(StringUtils.CONFIG_URL_TEAMS), new TypeReference<List<Team>>() {});
            //TeamManager.registerTeams(t);
            Logger.CONFIG.get().info("Config loaded !");
        } catch (IOException e) {
            Logger.CONFIG.get().log(new ConfigException("Can't load config !"));
        }
    }

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String outCfg = mapper.writeValueAsString(config);
            String outTeam = mapper.writeValueAsString(TeamManager.getTeams());

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
                        Logger.CONFIG.get().info("Successfully saved !");
                        break;
                }

                return response;
            });
        } catch (IOException e) {
            Logger.CONFIG.get().log(e);
        }
    }

    public static String getSetting(String s) {
        return config.get(s);
    }

    public static String setSetting(String key, String val) {
        return config.put(key, val);
    }
}
