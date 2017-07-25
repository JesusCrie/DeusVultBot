package com.jesus_crie.deusvult.response;

import com.jesus_crie.deusvult.utils.Awaiter;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import com.jesus_crie.deusvult.utils.T;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ResponsePaginable {

    private final ResponseBuilder builder;
    private final String title;
    private final List<ResponsePage> pages = new ArrayList<>();
    private Message current;
    private int currentPage = 0;
    private long timeout = T.calc(1, TimeUnit.MINUTES);

    public static ResponsePaginable create(Message m, String t) {
        return new ResponsePaginable(m, t);
    }

    private ResponsePaginable(Message m, String title) {
        builder = ResponseBuilder.create(m);
        this.title = title;
    }

    public ResponsePaginable setIcon(String url) {
        builder.setIcon(url);
        return this;
    }

    public ResponsePaginable setTimeout(long t) {
        timeout = t;
        return this;
    }

    public ResponsePaginable addPage(ResponsePage page) {
        pages.add(page);
        return this;
    }

    public ResponsePaginable addPages(ResponsePage... pages) {
        this.pages.addAll(Arrays.asList(pages));
        return this;
    }

    public ResponsePaginable setPage(int index) {
        if (index >= pages.size())
            index = pages.size() - 1;
        else if (index < 0)
            index = 0;

        currentPage = index;

        ResponsePage page = pages.get(index);
        builder.clearLists();
        builder.setTitle(S.RESPONSE_PAGINABLE_TITLE.format(title, currentPage + 1, pages.size()))
            .setMainList(page.getTitle(), Collections.emptyList())
            .setColor(page.getColor())
            .setDescription(page.getDescription());
        page.getFields().forEach(builder::addField);

        return this;
    }

    public void send(MessageChannel channel, User u) {
        setPage(currentPage);

        current = channel.sendMessage(builder.build()).complete();
        current.addReaction(StringUtils.EMOJI_PREVIOUS).complete();
        current.addReaction(StringUtils.EMOJI_REVERSE).complete();
        current.addReaction(StringUtils.EMOJI_NEXT).complete();

        Awaiter.awaitReactionFromUser(current, u,
                event -> {
                    switch (event.getReactionEmote().getName()) {
                        case StringUtils.EMOJI_PREVIOUS:
                            if (event.getTextChannel() != null)
                                event.getReaction().removeReaction(u).queue();
                            setPage(--currentPage);
                            current.editMessage(builder.build()).queue();
                            break;
                        case StringUtils.EMOJI_NEXT:
                            if (event.getTextChannel() != null)
                                event.getReaction().removeReaction(u).queue();
                            setPage(++currentPage);
                            current.editMessage(builder.build()).queue();
                            break;
                        case StringUtils.EMOJI_REVERSE:
                            if (event.getTextChannel() != null)
                                event.getReaction().removeReaction(u).queue();
                            setPage(0);
                            current.editMessage(builder.build()).queue();
                        default:
                            if (event.getTextChannel() != null)
                                event.getReaction().removeReaction(u).queue();
                            break;
                    }
                },
                () -> {
                    try {
                        current.clearReactions().queue();
                    } catch (Exception ignore) {}
                }, timeout);
    }
}
