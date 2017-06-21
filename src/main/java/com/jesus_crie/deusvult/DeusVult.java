package com.jesus_crie.deusvult;

import com.jesus_crie.deusvult.commands.*;
import com.jesus_crie.deusvult.config.Config;
import com.jesus_crie.deusvult.listener.CommandListener;
import com.jesus_crie.deusvult.logger.DiscordLog;
import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.manager.CommandManager;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

public class DeusVult {

    private JDA jda;
    private String secret;
    private boolean ready = false;
    private Guild main;

    public DeusVult(String token, String secret) {
        this.secret = secret;
        try {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setAudioEnabled(true)
                    .setAutoReconnect(true)
                    .setGame(Game.of("Starting..."))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .buildBlocking();
        } catch (LoginException login) {
            System.out.println("Login error: " + login);
        } catch (InterruptedException | RateLimitedException e) {
            // May never happened
            e.printStackTrace();
        }
        Logger.info("[Start] JDA initialized !");
    }

    void warmup() {
        Logger.info("[Start] Registering listeners...");
        jda.addEventListener(
                new CommandListener()
        );

        Logger.info("[Start] Loading config...");
        new Config(secret);

        Logger.info("[Start] Registering commands...");
        CommandManager.registerCommands(
                new TestCommand(),
                new StopCommand(),
                new DumpCommand(),
                new TeamCommand(),
                new EvalCommand()
        );

        Logger.info("[Start] Loading music components...");

        Logger.info("[Start] READY !");
        jda.getPresence().setGame(Game.of(StringUtils.PREFIX + "help - " + StringUtils.VERSION, "https://twitch.tv/discordapp"));

        DiscordLog.init(jda.getTextChannelById(Config.getSetting("channelLogs")));
        ready = true;
    }

    public void shutdown() {
        ready = false;
        Logger.info("[Start] Shutting down...");
        // TODO music stop
        Config.save();
        jda.shutdown(true);
    }

    public JDA getJda() {
        return jda;
    }

    public boolean isReady() {
        return ready;
    }

    public Guild getMainGuild() {
        if (main == null)
            main = jda.getGuildById(Config.getSetting("guildId"));
        return main;
    }

    public static DeusVult instance() {
        return Main.getBot();
    }
}
