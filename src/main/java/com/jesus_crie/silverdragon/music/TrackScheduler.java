package com.jesus_crie.silverdragon.music;

import com.jesus_crie.silverdragon.logger.Logger;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.LinkedList;

import static com.jesus_crie.silverdragon.utils.S.f;

public class TrackScheduler extends AudioEventAdapter {

    private final LinkedList<AudioTrack> queue = new LinkedList<>();
    private final AutoPlaylist auto;
    private final AudioPlayer player;

    public TrackScheduler(final AutoPlaylist auto, final AudioPlayer player) {
        this.auto = auto;
        this.player = player;
    }

    public void queue(AudioTrack track) {
        queue.add(track);
    }

    public void nextTrack() {
        if (queue.isEmpty())
            queue.add(auto.pick());
        player.playTrack(queue.pollFirst().makeClone());
    }

    public void stop() {
        player.stopTrack();
    }

    public void setPaused(final boolean state) {
        player.setPaused(state);
    }

    public void clear() {
        stop();
        queue.clear();
    }

    public void giveup() {
        clear();
        player.destroy();
    }

    @Override
    public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason) {
        if (endReason.mayStartNext)
            nextTrack();
    }

    @Override
    public void onTrackStuck(final AudioPlayer player, final AudioTrack track, final long thresholdMs) {
        Logger.MUSIC.get().warning(f("Track \"%s\" stuck in player for %", track.getInfo().uri, thresholdMs));
    }
}
