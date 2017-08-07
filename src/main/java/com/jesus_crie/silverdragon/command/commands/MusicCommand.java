package com.jesus_crie.silverdragon.command.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesus_crie.silverdragon.command.Command;
import com.jesus_crie.silverdragon.command.CommandPattern;
import com.jesus_crie.silverdragon.exception.CommandException;
import com.jesus_crie.silverdragon.manager.MusicManager;
import com.jesus_crie.silverdragon.music.GuildMusicManager;
import com.jesus_crie.silverdragon.music.ProvidablePlaylist;
import com.jesus_crie.silverdragon.response.ResponseBuilder;
import com.jesus_crie.silverdragon.response.ResponseUtils;
import com.jesus_crie.silverdragon.utils.F;
import com.jesus_crie.silverdragon.utils.StringUtils;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static com.jesus_crie.silverdragon.utils.S.f;

public class MusicCommand extends Command {

    public MusicCommand() {
        super("music",
                "Permet de gérer la musique.",
                null,
                AccessLevel.EVERYONE,
                Context.calculate(Context.MAIN_GUILD));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("stop")
                }, this::onStop, "stop"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("repeat")
                }, this::onRepeat, "repeat"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("current")
                }, this::onCurrent, "current"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("shuffle")
                }, this::onShuffle, "shuffle"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("queue")
                }, this::onQueue, "queue"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("pause")
                }, this::onPause, "pause"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("volume"),
                        CommandPattern.Argument.INTEGER
                }, this::onVolumeChange, "volume <volume>"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("volume")
                }, this::onVolume, "volume"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("skip")
                }, this::onSkip, "skip"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("play"),
                        CommandPattern.Argument.STRING
                }, this::onPlay, "play <url|identifier>"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("summon")
                }, this::onSummon, "summon")
        );
    }

    private GuildMusicManager getManager(MessageReceivedEvent event) {
        final GuildMusicManager manager = MusicManager.getManagerForGuild(event.getGuild());
        return manager == null ? MusicManager.registerGuild(event.getGuild()) : manager;
    }

    private boolean onSummon(MessageReceivedEvent event) {
        if (!AccessLevel.ADMIN.superiorOrEqual(AccessLevel.fromMember(event.getMember()))) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'avez pas la permission requise !"))
                    .send(event.getChannel()).complete();
            return true;
        }

        if (!MusicManager.isConnected(event.getMember())) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'êtes pas connecter à un channel vocal !"))
                    .send(event.getChannel()).complete();
            return true;
        }

        ResponseBuilder.create(event.getMessage())
                .setTitle("Connection à " + event.getMember().getVoiceState().getChannel().getName())
                .setIcon(StringUtils.ICON_MUSIC)
                .send(event.getChannel()).complete();

        getManager(event).connectToChannel(event.getMember().getVoiceState().getChannel());
        return true;
    }

    private boolean onPlay(MessageReceivedEvent event, List<Object> args) {
        final GuildMusicManager manager = getManager(event);

        if (!manager.isSameChannel(event.getMember())) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'êtes pas connecter au même channel que moi !"))
                    .send(event.getChannel()).complete();
            return true;
        }

        List<AudioTrack> tracks = MusicManager.loadTracks(((String) args.get(1)));
        if (tracks == null || tracks.size() <= 0) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException(f("Une erreur est survenue en chargeant '%s'", args.get(1))))
                    .send(event.getChannel()).complete();
            return true;
        }

        ResponseBuilder.create(event.getMessage())
                .setTitle("Musique")
                .setIcon(StringUtils.ICON_MUSIC)
                .addField(ResponseUtils.createList("Pistes ajoutés", false, tracks.stream()
                                .limit(30)
                                .map(t -> f(F.bold("%s") + " " + F.code("[%s]"), t.getInfo().title, StringUtils.properTimestamp(t.getInfo().length)))
                                .collect(Collectors.toList())))
                .send(event.getChannel()).complete();

        manager.getScheduler().queue(tracks);

        return true;
    }

    private boolean onSkip(MessageReceivedEvent event) {
        final GuildMusicManager manager = getManager(event);

        if (!manager.isSameChannel(event.getMember())) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'êtes pas connecter au même channel que moi !"))
                    .send(event.getChannel()).complete();
            return true;
        }

        ResponseBuilder.create(event.getMessage())
                .setTitle(f("Skipping \"%s\"", manager.getScheduler().getCurrent() == null ? "..." : manager.getScheduler().getCurrent().getInfo().title))
                .setIcon(StringUtils.ICON_MUSIC)
                .send(event.getChannel()).complete();

        manager.getScheduler().nextTrack();
        return true;
    }

    private boolean onVolume(MessageReceivedEvent event) {
        ResponseBuilder.create(event.getMessage())
                .setTitle(f("Volume: %s%%", getManager(event).getScheduler().getVolume()))
                .setIcon(StringUtils.ICON_MUSIC)
                .send(event.getChannel()).complete();
        return true;
    }

    private boolean onVolumeChange(MessageReceivedEvent event, List<Object> args) {
        final GuildMusicManager manager = getManager(event);

        if (!manager.isSameChannel(event.getMember())) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'êtes pas connecter au même channel que moi !"))
                    .send(event.getChannel()).complete();
            return true;
        }

        manager.getScheduler().setVolume(((Integer) args.get(1)));

        ResponseBuilder.create(event.getMessage())
                .setTitle(f("Le volume a été mis à %s%%", manager.getScheduler().getVolume()))
                .setIcon(StringUtils.ICON_MUSIC)
                .send(event.getChannel()).complete();
        return true;
    }

    private boolean onPause(MessageReceivedEvent event) {
        final GuildMusicManager manager = getManager(event);

        if (!manager.isSameChannel(event.getMember())) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'êtes pas connecter au même channel que moi !"))
                    .send(event.getChannel()).complete();
            return true;
        }

        boolean toPause = !manager.getScheduler().isPaused();
        manager.getScheduler().setPaused(toPause);

        ResponseBuilder.create(event.getMessage())
                .setTitle(toPause ? "Pause activée" : "Pause désactivée")
                .setIcon(StringUtils.ICON_MUSIC)
                .send(event.getChannel()).complete();
        return true;
    }

    private boolean onQueue(MessageReceivedEvent event) {
        final ProvidablePlaylist queue = getManager(event).getScheduler().getQueue();

        ResponseBuilder.create(event.getMessage())
                .setTitle("Pistes à venir")
                .setIcon(StringUtils.ICON_MUSIC)
                .setDescription(queue.isEmpty() ? "Aucune piste n'a été programmée." : queue.getTracks().stream()
                        .limit(30)
                        .map(t -> f(F.bold("%s") + " " + F.code("[%s]"), t.getInfo().title, StringUtils.properTimestamp(t.getInfo().length)))
                        .collect(Collectors.joining("\n" + StringUtils.EMOTE_DIAMOND_BLUE + " ",
                                StringUtils.EMOTE_DIAMOND_BLUE,
                                queue.getTracks().size() > 30 ? f("\net %s autres...", queue.getTracks().size() - 30) : "")))
                .send(event.getChannel()).complete();
        return true;
    }

    private boolean onShuffle(MessageReceivedEvent event) {
        ResponseBuilder.create(event.getMessage())
                .setTitle("Mélange de la queue en cours...")
                .setIcon(StringUtils.ICON_MUSIC)
                .send(event.getChannel()).complete();

        getManager(event).getScheduler().getQueue().shuffle();
        return true;
    }

    private boolean onCurrent(MessageReceivedEvent event) {
        final AudioTrack track = getManager(event).getScheduler().getCurrent();
        if (track == null) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Aucune piste n'est en cours."))
                    .send(event.getChannel()).complete();
            return true;
        }

        ResponseBuilder builder = ResponseBuilder.create(event.getMessage())
                .setTitle("Piste en cours")
                .setIcon(StringUtils.ICON_MUSIC)
                .setMainList(track.getInfo().title,
                        f("Auteur: %s", F.bold(track.getInfo().author)),
                        f("Temps: " + F.code("%s/%s"), StringUtils.properTimestamp(track.getPosition()), StringUtils.properTimestamp(track.getDuration())),
                        f("[" + F.bold("Source") + "](%s)", track.getInfo().uri));

        if (track instanceof YoutubeAudioTrack) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                final JsonNode nodeV = mapper.readValue(new URL(f(StringUtils.YOUTUBE_BASE_VIDEO, track.getIdentifier())), JsonNode.class);

                final JsonNode nodeC = mapper.readValue(new URL(
                        f(StringUtils.YOUTUBE_BASE_CHANNEL,
                        nodeV.get("items").get(0).get("snippet").get("channelId").asText("UCBR8-60-B28hp2BmDPdntcQ"))), JsonNode.class);
                builder.setThumbnail(nodeC.get("items").get(0).get("snippet").get("thumbnails").get("default").get("url").asText());
            } catch (IOException ignore) {
            } finally {
                builder.send(event.getChannel()).complete();
            }
        }
        return true;
    }

    private boolean onRepeat(MessageReceivedEvent event, List<Object> args) {
        final GuildMusicManager manager = getManager(event);
        if (!manager.isSameChannel(event.getMember())) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'êtes pas dans mon channel !"))
                    .send(event.getChannel()).complete();
            return true;
        }

        final AudioTrack track = getManager(event).getScheduler().getCurrent();
        if (track == null) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Aucune piste n'est en cours d'écoute"))
                    .send(event.getChannel()).complete();
            return true;
        }

        args.add(track.getIdentifier());
        return onPlay(event, args);
    }

    private boolean onStop(MessageReceivedEvent event) {
        final GuildMusicManager manager = getManager(event);
        if (!manager.isSameChannel(event.getMember())) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'êtes pas dans mon channel !"))
                    .send(event.getChannel()).complete();
            return true;
        }

        ResponseBuilder.create(event.getMessage())
                .setTitle("Deconnection...")
                .setIcon(StringUtils.ICON_MUSIC)
                .send(event.getChannel()).complete();

        manager.disconnect();
        return true;
    }
}
