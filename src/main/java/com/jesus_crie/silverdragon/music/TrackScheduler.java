package com.jesus_crie.silverdragon.music;

import com.jesus_crie.silverdragon.logger.Logger;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.List;

import static com.jesus_crie.silverdragon.utils.S.f;

public class TrackScheduler extends AudioEventAdapter {

    private final ProvidablePlaylist queue = new ProvidablePlaylist("Queue");
    private final AutoPlaylist auto;
    private final AudioPlayer player;

    public TrackScheduler(final AutoPlaylist auto, final AudioPlayer player) {
        this.auto = auto;
        this.player = player;
    }

    public void queue(AudioTrack track) {
        queue.offer(track);
    }

    public void queue(List<AudioTrack> tracks) {
        queue.offer(tracks);
    }

    public ProvidablePlaylist getQueue() {
        return queue;
    }

    public void nextTrack() {
        if (queue.isEmpty())
            queue.offer(auto.provideRandom());
        player.playTrack(queue.provide().makeClone());
    }

    public void stop() {
        player.stopTrack();
    }

    public boolean isPaused() {
        return player.isPaused();
    }

    public void setPaused(final boolean state) {
        player.setPaused(state);
    }

    public void clear() {
        stop();
        queue.clear();
    }

    public AudioTrack getCurrent() {
        return player.getPlayingTrack();
    }

    public int getVolume() {
        return player.getVolume();
    }

    public void setVolume(int v) {
        player.setVolume(v);
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
