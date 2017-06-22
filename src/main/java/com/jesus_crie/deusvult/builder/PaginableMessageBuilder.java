package com.jesus_crie.deusvult.builder;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.listener.ReactionListener;
import com.jesus_crie.deusvult.manager.TimerManager;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaginableMessageBuilder extends ReactableMessageBuilder {

    private String title;
    private String icon;
    private User requester;
    private List<EmbedPageBuilder> pages;
    private int currentPage = 0;
    private Message message;

    public PaginableMessageBuilder(User u) {
        super(u);
        requester = u;
        setReactions(StringUtils.EMOJI_PREVIOUS, StringUtils.EMOJI_NEXT);
        pages = new ArrayList<>();
    }

    @Override
    public void send(MessageChannel channel) {
        message = channel.sendMessage(build()).complete();
        listener = new ReactionListener(message.getIdLong(), event -> {
            if (!event.getUser().equals(requester))
                return;

            switch (event.getReaction().getEmote().getName()) {
                case StringUtils.EMOJI_NEXT:
                    nextPage();
                    break;
                case StringUtils.EMOJI_PREVIOUS:
                    previousPage();
                    break;
                default: // Other emote
                    break;
            }

            try {
                event.getReaction().removeReaction(requester).queue();
            } catch (PermissionException e) {}
        });
        DeusVult.instance().getJda().addEventListener(listener);

        emotes.forEach(e -> message.addReaction(e).complete());
        TimerManager.doLater(() -> {
            DeusVult.instance().getJda().removeEventListener(listener);
            message.clearReactions().queue();
        }, 60 * 1000);
    }

    public void nextPage() {
        setPage(currentPage + 1);
    }

    public void previousPage() {
        setPage(currentPage - 1);
    }

    public void setPage(int index) {
        if (index == currentPage)
            return;
        else if (index > 0)
            currentPage = 0;
        else if (index <= pages.size())
            currentPage = pages.size() - 1;
        else
            currentPage = index;

        message.editMessage(build()).queue();
    }

    public PaginableMessageBuilder addPages(EmbedPageBuilder... pages) {
        this.pages.addAll(Arrays.asList(pages));
        return this;
    }

    public PaginableMessageBuilder addPage(EmbedPageBuilder page) {
        pages.add(page);
        return this;
    }

    @Override
    public PaginableMessageBuilder setTitle(String title, String icon) {
        this.title = title;
        this.icon = icon;
        return this;
    }

    @Override
    public MessageEmbed build() {
        MessageEmbed page = pages.get(currentPage).build();

        setAuthor(title + " (" + (currentPage + 1) + "/" + pages.size() + ")", null, icon);

        setDescription(page.getDescription());
        setTitle(page.getTitle(), page.getUrl());

        if (page.getThumbnail() != null)
            setThumbnail(page.getThumbnail().getUrl());
        else
            setThumbnail(null);

        if (page.getImage() != null)
            setImage(page.getImage().getUrl());
        else
            setImage(null);

        clearFields();
        page.getFields().forEach(this::addField);

        return super.build();
    }
}
