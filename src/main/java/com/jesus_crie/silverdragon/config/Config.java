package com.jesus_crie.silverdragon.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesus_crie.silverdragon.exception.ConfigException;
import com.jesus_crie.silverdragon.logger.Logger;
import com.jesus_crie.silverdragon.manager.LobbyManager;
import com.jesus_crie.silverdragon.utils.StringUtils;
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

            List<Lobby> t = mapper.readValue(new URL(StringUtils.CONFIG_URL_TEAMS), new TypeReference<List<Lobby>>() {});
            LobbyManager.registerLobbies(t);
            Logger.CONFIG.get().info("Config loaded !");
        } catch (IOException e) {
            Logger.CONFIG.get().trace(new ConfigException("Can't load config !"));
        }
    }

    public static void save() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String outCfg = mapper.writeValueAsString(config);
            String outTeam = mapper.writeValueAsString(LobbyManager.getLobbies());

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
                        Logger.CONFIG.get().info("Successfully saved !");
                        break;
                    default:
                        Logger.CONFIG.get().warning("WTF ??");
                        break;
                }

                return response;
            });
        } catch (IOException e) {
            Logger.CONFIG.get().trace(e);
        }
    }

    public static String getSetting(String s) {
        return config.get(s);
    }

    public static String setSetting(String key, String val) {
        return config.put(key, val);
    }
}
