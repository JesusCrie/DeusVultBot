package com.jesus_crie.deusvult.response;

import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResponsePage {

    private final String title;
    private String description;
    private Color color;
    private final List<MessageEmbed.Field> fields;

    public ResponsePage(String title) {
        color = Color.WHITE;
        this.title = title;
        description = "";
        fields = new ArrayList<>();
    }

    public ResponsePage setDescription(String desc) {
        description = desc;
        return this;
    }

    public ResponsePage setColor(Color c) {
        color = c;
        return this;
    }

    public ResponsePage addField(String name, String content, boolean inline) {
        fields.add(new MessageEmbed.Field(name, content, inline));
        return this;
    }

    public ResponsePage addFields(MessageEmbed.Field... fields) {
        this.fields.addAll(Arrays.asList(fields));
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Color getColor() {
        return color;
    }

    public List<MessageEmbed.Field> getFields() {
        return fields;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResponsePage && ((ResponsePage) obj).title.equals(title);
    }
}
