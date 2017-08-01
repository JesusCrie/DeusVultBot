package com.jesus_crie.silverdragon.music;

import com.jesus_crie.silverdragon.logger.Logger;
import com.jesus_crie.silverdragon.manager.MusicManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;
import java.util.Random;

public class AutoPlaylist {

    private final Random random = new Random();
    private final List<AudioTrack> tracks;

    public AutoPlaylist(String identifier) {
        tracks = MusicManager.loadTracks(identifier);
        if (tracks.size() <= 0)
            Logger.MUSIC.get().warning("Failed to load AutoPlaylist !");
    }

    public AudioTrack pick() {
        return tracks.get(random.nextInt(tracks.size())).makeClone();
    }
}
