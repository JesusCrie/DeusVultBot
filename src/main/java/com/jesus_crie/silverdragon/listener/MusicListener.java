package com.jesus_crie.silverdragon.listener;

import com.jesus_crie.silverdragon.logger.Logger;
import com.jesus_crie.silverdragon.manager.MusicManager;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import static com.jesus_crie.silverdragon.utils.S.f;

public class MusicListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (!MusicManager.getManagerForGuild(event.getGuild()).isConnected())
            return;
        if (!MusicManager.getManagerForGuild(event.getGuild()).isSameChannel(event.getMember()))
            return;
        trigger(event);
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (!MusicManager.getManagerForGuild(event.getGuild()).isConnected())
            return;
        trigger(event);
    }

    private void trigger(GenericGuildVoiceEvent event) {
        int size = MusicManager.getManagerForGuild(event.getGuild()).getConnectedChannel().getMembers().size();
        Logger.MUSIC.get().info(f("Size: %s", size));

        if (size == 2) {
            MusicManager.getManagerForGuild(event.getGuild()).getScheduler().setPaused(false);
            Logger.MUSIC.get().info(f("[%s] Resuming player", event.getGuild().getName()));
        } else if (size == 1) {
            MusicManager.getManagerForGuild(event.getGuild()).getScheduler().setPaused(true);
            Logger.MUSIC.get().info(f("[%s] Pausing player", event.getGuild().getName()));
        }
    }
}
