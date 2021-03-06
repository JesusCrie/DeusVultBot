package com.jesus_crie.silverdragon.response;

import com.jesus_crie.silverdragon.utils.StringUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.RestAction;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.jesus_crie.silverdragon.utils.S.f;

public class ResponseBuilder {

    public final static SimpleDateFormat TIME = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private StringBuilder mentions = new StringBuilder();
    private String title;
    private String icon;
    private final EmbedBuilder builder = new EmbedBuilder();

    public static ResponseBuilder create(User author, Instant timestamp) {
        return new ResponseBuilder(author, timestamp);
    }

    public static ResponseBuilder create(Message m) {
        return new ResponseBuilder(m.getAuthor(), m.getCreationTime().toInstant());
    }

    private ResponseBuilder(User author, Instant timestamp) {
        builder.setFooter(f("%s [%s]", StringUtils.stringifyUser(author), TIME.format(new Date())), author.getEffectiveAvatarUrl());
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
        return setMainList(title, Arrays.asList(((Object[]) content)));
    }

    public ResponseBuilder setMainList(String title, List<Object> content) {
        builder.setTitle(title);
        if (content != null && !content.isEmpty())
            builder.setDescription(StringUtils.EMOTE_DIAMOND_ORANGE + String.join("\n" + StringUtils.EMOTE_DIAMOND_ORANGE,
                content.stream().map(Object::toString).collect(Collectors.toList())));
        return this;
    }

    public ResponseBuilder addField(String title, String content, boolean inline) {
        builder.addField(title, content, inline);
        return this;
    }

    public ResponseBuilder addField(MessageEmbed.Field field) {
        builder.addField(field);
        return this;
    }

    public ResponseBuilder addMention(IMentionable mentionable) {
        mentions.append(mentionable.getAsMention());
        mentions.append(" ");
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

    EmbedBuilder getBuilder() {
        return builder;
    }

    public RestAction<Message> send(MessageChannel channel) {
        if (mentions.length() > 0) {
            Message m = new MessageBuilder().setEmbed(builder.build())
                    .append(mentions.toString())
                    .build();
            return channel.sendMessage(m);
        } else
            return channel.sendMessage(build());
    }

    public MessageEmbed build() {
        return builder.build();
    }
}
