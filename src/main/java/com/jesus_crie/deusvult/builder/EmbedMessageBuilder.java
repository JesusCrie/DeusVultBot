package com.jesus_crie.deusvult.builder;

import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;

import java.awt.Color;
import java.util.List;

public class EmbedMessageBuilder extends EmbedBuilder {

    public EmbedMessageBuilder(User u) {
        super.setFooter("Requested by " + StringUtils.stringifyUser(u), u.getEffectiveAvatarUrl());
        setColor(Color.WHITE);
    }

    public EmbedMessageBuilder addFieldList(String name, String... content) {
        addField(name, StringUtils.EMOJI_DIAMOND_BLUE + String.join("\n" + StringUtils.EMOJI_DIAMOND_BLUE, content), false);
        return this;
    }

    public EmbedMessageBuilder addFieldList(String name, List<String> content) {
        addField(name, StringUtils.EMOJI_DIAMOND_BLUE + String.join("\n" + StringUtils.EMOJI_DIAMOND_BLUE, content), false);
        return this;
    }

    public EmbedMessageBuilder addMainList(String name, String icon, String... content) {
        setAuthor(name, null, icon);
        setDescription(StringUtils.EMOJI_DIAMOND_ORANGE + String.join("\n" + StringUtils.EMOJI_DIAMOND_ORANGE, content));
        return this;
    }

    public EmbedMessageBuilder addMainList(String name, String icon, List<String> content) {
        setAuthor(name, null, icon);
        setDescription(StringUtils.EMOJI_DIAMOND_ORANGE + String.join("\n" + StringUtils.EMOJI_DIAMOND_ORANGE, content));
        return this;
    }

    @Deprecated
    @Override
    public EmbedBuilder setFooter(String text, String iconUrl) {
        return null;
    }
}
