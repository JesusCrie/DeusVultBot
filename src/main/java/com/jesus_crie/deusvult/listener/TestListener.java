package com.jesus_crie.deusvult.listener;

import com.jesus_crie.deusvult.builder.EmbedPageBuilder;
import com.jesus_crie.deusvult.builder.PaginableMessageBuilder;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TestListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(StringUtils.USER_CREATOR))
            return;
        if (!event.getMessage().getRawContent().equalsIgnoreCase("test"))
            return;

        PaginableMessageBuilder builder = new PaginableMessageBuilder(event.getAuthor());
        EmbedPageBuilder page1 = new EmbedPageBuilder();
        EmbedPageBuilder page2 = new EmbedPageBuilder();

        builder.setTitle("Test Paginable", StringUtils.ICON_TERMINAL);
        page1.setTitle("Page 1");
        page2.setTitle("Page 2");

        builder.addPages(page1, page2);

        builder.send(event.getChannel());
    }
}
