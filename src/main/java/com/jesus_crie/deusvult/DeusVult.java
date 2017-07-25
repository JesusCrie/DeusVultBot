package com.jesus_crie.deusvult;

import com.jesus_crie.deusvult.command.commands.*;
import com.jesus_crie.deusvult.config.Config;
import com.jesus_crie.deusvult.listener.CommandListener;
import com.jesus_crie.deusvult.logger.DiscordLogListener;
import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.manager.CommandManager;
import com.jesus_crie.deusvult.manager.ThreadManager;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

import static com.jesus_crie.deusvult.utils.S.*;

public class DeusVult {

    private JDA jda;
    private final String secret;
    private boolean ready = false;
    private Guild main;
    private final long start;

    public DeusVult(String token, String secret) {
        start = System.currentTimeMillis();

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
        Logger.START.get().info("JDA initialized !");
    }

    void warmup() {
        Logger.START.get().info("Registering listeners...");
        jda.addEventListener(
                new CommandListener()
        );

        Logger.START.get().info("Loading config...");
        new Config(secret);

        Logger.START.get().info("Registering commands...");
        CommandManager.registerCommands(
                // Global
                new HelpCommand(),
                new PingCommand(),

                // General
                new QuoteCommand(),
                new WordReactCommand(),
                new EightBallCommand(),
                new GifCommand(),
                new MathCommand(),
                new InfoCommand(),

                // Moderation
                new ClearCommand(),
                new StopCommand(),
                new UserInfoCommand(),
                new EvalCommand(),
                new AdminCommand(),
                new TestCommand(),

                // Experimental
                new TeamCommand()
        );

        Logger.START.get().info("Loading music components...");

        Logger.START.get().info("READY !");
        jda.getPresence().setGame(Game.of(S("%shelp - v%s", StringUtils.PREFIX, StringUtils.VERSION), "https://twitch.tv/discordapp"));

        Logger.SimpleLogger.addListener(new DiscordLogListener(jda.getTextChannelById(Config.getSetting("channelLogs"))));
        Logger.START.get().info("Discord logging set !");

        ready = true;
    }

    public void shutdown() {
        ready = false;
        Logger.START.get().info("Shutting down...");
        // TODO music stop
        ThreadManager.cleanUp();
        Config.save();
        jda.shutdownNow();
    }

    // Shutdown as fast as it can
    public void forceShutdown() {
        ready = false;
        Logger.START.get().warning("Force shutting down !");
        jda.shutdownNow();
        System.exit(1);
    }

    public JDA getJDA() {
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

    public User getUserByNameDiscriminator(String name, String discriminator) {
        return jda.getUsersByName(name, true).stream()
                .filter(u -> u.getDiscriminator().equals(discriminator))
                .findFirst()
                .orElse(null);
    }

    public long getStart() {
        return start;
    }

    public static DeusVult instance() {
        return Main.getBot();
    }
}
