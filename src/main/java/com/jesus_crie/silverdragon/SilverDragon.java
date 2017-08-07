package com.jesus_crie.silverdragon;

import com.jesus_crie.silverdragon.command.commands.*;
import com.jesus_crie.silverdragon.config.Config;
import com.jesus_crie.silverdragon.listener.CommandListener;
import com.jesus_crie.silverdragon.listener.MusicListener;
import com.jesus_crie.silverdragon.logger.DiscordLogListener;
import com.jesus_crie.silverdragon.logger.Logger;
import com.jesus_crie.silverdragon.manager.CommandManager;
import com.jesus_crie.silverdragon.manager.MusicManager;
import com.jesus_crie.silverdragon.manager.ThreadManager;
import com.jesus_crie.silverdragon.utils.StringUtils;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

import static com.jesus_crie.silverdragon.utils.S.f;

public class SilverDragon {

    private JDA jda;
    private final String secret;
    private boolean ready = false;
    private Guild main;
    private final long start;

    public SilverDragon(String token, String secret) {
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

    void wakeup() {
        Logger.START.get().info("Registering listeners...");
        jda.addEventListener(
                new CommandListener(),
                new MusicListener()
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
                new LobbyCommand(),
                new MusicCommand()
        );

        Logger.START.get().info("Loading music components...");
        new MusicManager();
        jda.getGuilds().forEach(MusicManager::registerGuild);

        Logger.START.get().info("READY !");
        jda.getPresence().setGame(Game.of(f("%shelp - v%s", StringUtils.PREFIX, StringUtils.VERSION)));
        jda.getPresence().setStatus(OnlineStatus.ONLINE);

        Logger.SimpleLogger.addListener(new DiscordLogListener(jda.getTextChannelById(Config.getSetting("channelLogs"))));
        Logger.START.get().info("Discord logging set !");

        ready = true;
    }

    public void shutdown() {
        ready = false;
        Logger.START.get().info("Shutting down...");
        MusicManager.getManagers().forEach((k, m) -> m.cleanup());
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

    public static SilverDragon instance() {
        return Main.getBot();
    }
}
