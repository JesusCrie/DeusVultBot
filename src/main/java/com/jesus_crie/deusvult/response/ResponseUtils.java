package com.jesus_crie.deusvult.response;

import com.jesus_crie.deusvult.exception.CommandException;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.entities.Message;

import java.awt.*;

public class ResponseUtils {

    public static ResponseBuilder errorMessage(Message m, CommandException e) {
        return ResponseBuilder.create(m)
                .setColor(Color.RED)
                .setIcon(StringUtils.ICON_ERROR)
                .setTitle("An error occured !")
                .setDescription(e.toString());
    }
}
