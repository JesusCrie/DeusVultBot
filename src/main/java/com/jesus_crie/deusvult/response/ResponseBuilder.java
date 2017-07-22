package com.jesus_crie.deusvult.response;

import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RestAction;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseBuilder {

    private String title;
    private String icon;
    private EmbedBuilder builder = new EmbedBuilder();

    public static ResponseBuilder create(User author, Instant timestamp) {
        return new ResponseBuilder(author, timestamp);
    }

    public static ResponseBuilder create(Message m) {
        return new ResponseBuilder(m.getAuthor(), m.getCreationTime().toInstant());
    }

    protected ResponseBuilder(User author, Instant timestamp) {
        builder.setAuthor(S.RESPONSE_FOOTER.format(StringUtils.stringifyUser(author)), null, author.getEffectiveAvatarUrl());
        builder.setTimestamp(timestamp);
        builder.setColor(Color.WHITE);
    }

    public ResponseBuilder setTitle(String title) {
        this.title = title;
        builder.setAuthor(title, null, icon);
        return this;
    }

    public ResponseBuilder setIcon(String url) {
        icon = url;
        builder.setAuthor(title, null, icon);
        return this;
    }

    public ResponseBuilder setDescription(String desc) {
        builder.setDescription(desc);
        return this;
    }

    public ResponseBuilder setMainList(String title, String... content) {
        return setMainList(title, Arrays.asList(content));
    }

    public ResponseBuilder setMainList(String title, List<Object> content) {
        builder.setTitle(title);
        builder.setDescription(StringUtils.EMOJI_DIAMOND_ORANGE + String.join("\n" + StringUtils.EMOJI_DIAMOND_ORANGE,
                content.stream().map(Object::toString).collect(Collectors.toList())));
        return this;
    }

    public ResponseBuilder addList(String title, boolean inline, String... content) {
        return addList(title, inline, Arrays.asList(content));
    }

    public ResponseBuilder addList(String title, boolean inline, List<String> content) {
        if (content.isEmpty())
            builder.addField(title, null, inline);
        else
            builder.addField(title,
                    StringUtils.EMOJI_DIAMOND_BLUE + String.join("\n" + StringUtils.EMOJI_DIAMOND_BLUE, content),
                    inline);
        return this;
    }

    public ResponseBuilder addField(String title, String content, boolean inline) {
        builder.addField(title, content, inline);
        return this;
    }

    public ResponseBuilder clearLists() {
        builder.clearFields();
        return this;
    }

    public ResponseBuilder setThumbnail(String url) {
        builder.setThumbnail(url);
        return this;
    }

    public ResponseBuilder setImage(String url) {
        builder.setImage(url);
        return this;
    }

    public ResponseBuilder setColor(Color c) {
        builder.setColor(c);
        return this;
    }

    public RestAction<Message> send(MessageChannel channel) {
        return channel.sendMessage(builder.build());
    }
}
