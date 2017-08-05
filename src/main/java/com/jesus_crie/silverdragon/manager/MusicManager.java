package com.jesus_crie.silverdragon.manager;

import com.jesus_crie.silverdragon.logger.Logger;
import com.jesus_crie.silverdragon.music.AutoPlaylist;
import com.jesus_crie.silverdragon.music.GuildMusicManager;
import com.jesus_crie.silverdragon.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static com.jesus_crie.silverdragon.utils.S.f;

public class MusicManager {

    private static final AudioPlayerManager globalManager = new DefaultAudioPlayerManager();
    private static final HashMap<Long, GuildMusicManager> managers = new HashMap<>();

    public MusicManager() {
        AudioSourceManagers.registerLocalSource(globalManager);
        AudioSourceManagers.registerRemoteSources(globalManager);
    }

    public static HashMap<Long, GuildMusicManager> getManagers() {
        return managers;
    }

    public static GuildMusicManager getManagerForGuild(Guild g) {
        return managers.getOrDefault(g.getIdLong(), registerGuild(g));
    }

    public static GuildMusicManager registerGuild(Guild g) {
        GuildMusicManager manager = new GuildMusicManager(g,
                new AutoPlaylist(StringUtils.MUSIC_DEFAULT_PLAYLIST),
                globalManager.createPlayer());
        managers.put(g.getIdLong(), manager);
        return manager;
    }

    @SuppressWarnings({"finally", "ReturnInsideFinallyBlock"})
    public static List<AudioTrack> loadTracks(final String identifier) {
        final List<AudioTrack> out = new ArrayList<>();

        final AudioLoadResultHandler handler = createHandler(
                out::add,
                playlist -> out.addAll(playlist.getTracks()),
                () -> Logger.MUSIC.get().warning(f("Fail to load %s, no matches", identifier)),
                e -> Logger.MUSIC.get().trace(e)
        );

        Future<Void> loader = globalManager.loadItem(identifier, handler);
        try {
            loader.get();
        } catch (Exception ignore) {
        } finally {
            return out;
        }
    }

    public static AudioLoadResultHandler createHandler(final Consumer<AudioTrack> onSingleTrack,
                                                       final Consumer<AudioPlaylist> onPlaylist,
                                                       final Runnable onFailNoMatch,
                                                       final Consumer<FriendlyException> onError) {
        return new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (onSingleTrack != null)
                    onSingleTrack.accept(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (onPlaylist != null)
                    onPlaylist.accept(playlist);
            }

            @Override
            public void noMatches() {
                if (onFailNoMatch != null)
                    onFailNoMatch.run();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (onError != null)
                    onError.accept(exception);
            }
        };
    }

    public static boolean isConnected(Member m) {
        return m.getVoiceState().inVoiceChannel();
    }
}
