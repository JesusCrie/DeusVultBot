package com.jesus_crie.silverdragon.music;

import com.jesus_crie.silverdragon.logger.Logger;
import com.jesus_crie.silverdragon.manager.MusicManager;

import java.util.LinkedList;
import java.util.Random;

public class AutoPlaylist extends ProvidablePlaylist {

    private final Random random = new Random();

    public AutoPlaylist(String identifier) {
        super("AutoPlatlist: " + identifier, new LinkedList<>(MusicManager.loadTracks(identifier)));
        if (tracks.size() <= 0)
            Logger.MUSIC.get().warning("Failed to load AutoPlaylist !");
    }
}
