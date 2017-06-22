package com.jesus_crie.deusvult.builder;

import com.jesus_crie.deusvult.DeusVult;
import net.dv8tion.jda.core.EmbedBuilder;

public class EmbedPageBuilder extends EmbedMessageBuilder {

    public EmbedPageBuilder() {
        super(DeusVult.instance().getJda().getSelfUser());
    }

    @Deprecated
    @Override
    public EmbedBuilder setAuthor(String name, String url, String iconUrl) {
        return null;
    }
}
