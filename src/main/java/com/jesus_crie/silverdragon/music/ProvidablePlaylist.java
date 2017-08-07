package com.jesus_crie.silverdragon.music;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ProvidablePlaylist implements AudioPlaylist, Cloneable {

    private final String name;
    protected final LinkedList<AudioTrack> tracks;

    public ProvidablePlaylist(String name, LinkedList<AudioTrack> tracks) {
        if (name == null || name.isEmpty())
            name = "Unknown";
        this.name = name;
        this.tracks = tracks;
    }

    public ProvidablePlaylist(String name) {
        if (name == null || name.isEmpty())
            name = "Unknown";
        this.name = name;
        this.tracks = new LinkedList<>();
    }

    public AudioTrack provide() {
        return tracks.pollFirst();
    }

    public AudioTrack provideRandom() {
        shuffle();
        return provide();
    }

    public void offer(AudioTrack track) {
        tracks.offerLast(track);
    }

    public void offer(List<AudioTrack> tracks) {
        this.tracks.addAll(tracks);
    }

    public void offerFirst(AudioTrack track) {
        tracks.offerFirst(track);
    }

    public void shuffle() {
        Collections.shuffle(tracks);
    }

    public void clear() {
        tracks.clear();
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isEmpty() {
        return tracks.isEmpty();
    }

    @Override
    public List<AudioTrack> getTracks() {
        return tracks;
    }

    @Override
    public AudioTrack getSelectedTrack() {
        return provide();
    }

    @Override
    public boolean isSearchResult() {
        return false;
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (Exception ignore) {
            return null;
        }
    }
}
