package com.jesus_crie.silverdragon.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class GuildMusicManager {

    private final Guild guild;
    private TrackScheduler scheduler;

    public GuildMusicManager(final Guild guild, final AutoPlaylist auto, final AudioPlayer player) {
        this.guild = guild;
        scheduler = new TrackScheduler(auto, player);
    }

    public void connectToChannel(VoiceChannel channel) {
        if (!guild.getAudioManager().isAttemptingToConnect())
            guild.getAudioManager().openAudioConnection(channel);
    }

    public void disconnect() {
        scheduler.setPaused(true);
        guild.getAudioManager().closeAudioConnection();
    }

    public boolean isConnected() {
        return guild.getAudioManager().isConnected();
    }

    public void reload(final AutoPlaylist auto, final AudioPlayer player) {
        cleanup();
        scheduler = new TrackScheduler(auto, player);
    }

    public void cleanup() {
        disconnect();
        scheduler.giveup();
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }
}