package com.jesus_crie.deusvult.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jesus_crie.deusvult.DeusVult;
import net.dv8tion.jda.core.entities.*;

import java.util.List;
import java.util.stream.Collectors;

public class Team {

    private int id;
    private String name;
    private Role role;
    private User owner;
    private TextChannel channelText;
    private VoiceChannel channelVoice;
    private List<User> members;

    @JsonCreator
    public Team(@JsonProperty("id") int id,
                @JsonProperty("name") String name,
                @JsonProperty("roleId") String roleId,
                @JsonProperty("ownerId") String ownerId,
                @JsonProperty("channelTextId") String channelTextId,
                @JsonProperty("channelVoiceId") String channelVoiceId,
                @JsonProperty("membersId")List<String> membersId) {
        this.id = id;
        this.name = name;
        role = DeusVult.instance().getJda().getRoleById(roleId);
        owner = DeusVult.instance().getJda().getUserById(ownerId);
        channelText = DeusVult.instance().getJda().getTextChannelById(channelTextId);
        channelVoice = DeusVult.instance().getJda().getVoiceChannelById(channelVoiceId);
        members = membersId
                .stream()
                .map(m -> DeusVult.instance().getJda().getUserById(m))
                .collect(Collectors.toList());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public User getOwner() {
        return owner;
    }

    public TextChannel getChannelText() {
        return channelText;
    }

    public VoiceChannel getChannelVoice() {
        return channelVoice;
    }

    public List<User> getMembers() {
        return members;
    }
}
