package com.jesus_crie.deusvult;

import com.jesus_crie.deusvult.commands.DumpCommand;
import com.jesus_crie.deusvult.commands.StopCommand;
import com.jesus_crie.deusvult.commands.TeamCommand;
import com.jesus_crie.deusvult.commands.TestCommand;
import com.jesus_crie.deusvult.config.Config;
import com.jesus_crie.deusvult.config.Team;
import com.jesus_crie.deusvult.listener.CommandListener;
import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.manager.CommandManager;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class DeusVult {

    private JDA jda;
    private String secret;
    private boolean ready = false;

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
    }

    void warmup() {
        Logger.info("[Start] Registering listeners...");
        jda.addEventListener(
                new CommandListener()
        );

        Logger.info("[Start] Loading config...");
        new Config(secret);
        Team team = new Team(0, "Les petits penis", "323951853807206401", "182547138729869314",
                "323950520949800961", "323950657226670081", Arrays.asList("220263485810933760", "200156541746151424"));
        Team t2 = new Team(1, "Assholes", "323952347740897290", "200156541746151424",
                "323950568756477973", "323950685567713290", Arrays.asList("220263485810933760", "182547138729869314"));
        Config.saveTeam(team);
        Config.saveTeam(t2);

        Logger.info("[Start] Registering commands...");
        CommandManager.registerCommands(
                new TestCommand(),
                new StopCommand(),
                new DumpCommand(),
                new TeamCommand()
        );

        Logger.info("[Start] Loading music components...");

        Logger.info("[Start] READY !");
        jda.getPresence().setGame(Game.of(StringUtils.PREFIX + "help - " + StringUtils.VERSION, "https://twitch.tv/discordapp"));
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

    public static DeusVult instance() {
        return Main.getBot();
    }
}
