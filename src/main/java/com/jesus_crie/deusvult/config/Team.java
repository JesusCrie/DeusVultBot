package com.jesus_crie.deusvult.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.entities.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@JsonSerialize(using = Team.TeamSerializer.class)
public class Team {

    private int id;
    private String name;
    private Role role;
    private User owner;
    private TextChannel channelText;
    private VoiceChannel channelVoice;
    private List<User> members;

    @JsonCreator
    private Team(@JsonProperty("id") int id,
                @JsonProperty("name") String name,
                @JsonProperty("roleId") String roleId,
                @JsonProperty("ownerId") String ownerId,
                @JsonProperty("channelTextId") String channelTextId,
                @JsonProperty("channelVoiceId") String channelVoiceId) {
        this.id = id;
        this.name = name;
        role = DeusVult.instance().getJda().getRoleById(roleId);
        owner = DeusVult.instance().getJda().getUserById(ownerId);
        channelText = DeusVult.instance().getJda().getTextChannelById(channelTextId);
        channelVoice = DeusVult.instance().getJda().getVoiceChannelById(channelVoiceId);
        members = role.getGuild().getMembersWithRoles(role).stream()
                .map(Member::getUser)
                .collect(Collectors.toList());
    }

    public Team(int id, String name, Role role, User owner, TextChannel channelText, VoiceChannel channelVoice) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.owner = owner;
        this.channelText = channelText;
        this.channelVoice = channelVoice;
        members = role.getGuild().getMembersWithRoles(role).stream()
                .map(Member::getUser)
                .collect(Collectors.toList());
    }

    public boolean isMember(User u) {
        return members.contains(u);
    }

    public boolean isOwner(User u) {
        return owner.equals(u);
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

    @Override
    public boolean equals(Object obj) {
        return ((Team) obj).getId() == id;
    }

    public static class TeamSerializer extends StdSerializer<Team> {

        public TeamSerializer() {
            this(null);
        }

        public TeamSerializer(Class<Team> team) {
            super(team);
        }

        @Override
        public void serialize(Team value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();

            gen.writeNumberField("id", value.id);
            gen.writeStringField("name", value.name);
            gen.writeStringField("roleId", value.role.getId());
            gen.writeStringField("ownerId", value.owner.getId());
            gen.writeStringField("channelTextId", value.channelText.getId());
            gen.writeStringField("channelVoiceId", value.channelVoice.getId());

            gen.writeEndObject();
        }
    }
}
