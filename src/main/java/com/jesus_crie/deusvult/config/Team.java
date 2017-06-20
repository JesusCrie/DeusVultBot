package com.jesus_crie.deusvult.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.jesus_crie.deusvult.DeusVult;
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

            gen.writeArrayFieldStart("membersId");
            for (User m : value.members)
                gen.writeString(m.getId());
            gen.writeEndArray();

            gen.writeEndObject();
        }
    }
}
