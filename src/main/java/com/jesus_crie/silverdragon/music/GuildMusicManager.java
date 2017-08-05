package com.jesus_crie.silverdragon.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class GuildMusicManager {

    private final Guild guild;
    private TrackScheduler scheduler;

    public GuildMusicManager(final Guild guild, final AutoPlaylist auto, final AudioPlayer player) {
        this.guild = guild;
        this.guild.getAudioManager().setSendingHandler(new JDAAudioSendHandler(player));
        scheduler = new TrackScheduler(auto, player);
    }

    public void connectToChannel(VoiceChannel channel) {
        if (!guild.getAudioManager().isAttemptingToConnect()) {
            guild.getAudioManager().openAudioConnection(channel);
            guild.getAudioManager().setSelfDeafened(true);
            getScheduler().nextTrack();
        }
    }

    public void disconnect() {
        scheduler.setPaused(true);
        guild.getAudioManager().closeAudioConnection();
    }

    public boolean isConnected() {
        return guild.getAudioManager().isConnected();
    }


    public boolean isSameChannel(Member m) {
        if (!isConnected())
            return false;
        return guild.getAudioManager().getConnectedChannel().getMembers().contains(m);
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
