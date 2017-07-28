package com.jesus_crie.deusvult.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.manager.ThreadManager;
import net.dv8tion.jda.core.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jesus_crie.deusvult.utils.S.*;

@JsonSerialize(using = Team.TeamSerializer.class)
public class Team implements Comparable<Team> {

    private final int id;
    private String name;
    private final Role role;
    private User owner;
    private final TextChannel channelText;
    private final VoiceChannel channelVoice;
    private List<User> members = new ArrayList<>();

    @JsonCreator
    private Team(@JsonProperty("id") int id,
                @JsonProperty("name") String name,
                @JsonProperty("roleId") String roleId,
                @JsonProperty("ownerId") String ownerId,
                @JsonProperty("channelTextId") String channelTextId,
                @JsonProperty("channelVoiceId") String channelVoiceId) {
        this.id = id;
        this.name = name;
        role = DeusVult.instance().getJDA().getRoleById(roleId);
        owner = DeusVult.instance().getJDA().getUserById(ownerId);
        channelText = DeusVult.instance().getJDA().getTextChannelById(channelTextId);
        channelVoice = DeusVult.instance().getJDA().getVoiceChannelById(channelVoiceId);
        ThreadManager.getGeneralPool().execute(this::update);
    }

    public Team(int id, String name, Role role, User owner, TextChannel channelText, VoiceChannel channelVoice) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.owner = owner;
        this.channelText = channelText;
        this.channelVoice = channelVoice;
        ThreadManager.getGeneralPool().execute(this::update);
    }

    public void update() {
        role.getManager().setName(f("Team - %s", name)).complete();
        channelText.getManagerUpdatable().getNameField().setValue(f("team-%s", name.replace(" ", "_")))
                .getTopicField().setValue(f("Channel de la team %s", name)).update().complete();
        channelVoice.getManager().setName(f("\uD83C\uDF0F Team - %s", name)).complete();
        members = role.getGuild().getMembersWithRoles(role).stream()
                .map(Member::getUser)
                .collect(Collectors.toList());
    }

    public void delete() {
        channelText.delete().complete();
        channelVoice.delete().complete();
        role.delete().complete();
    }

    public void addMember(User u) {
        if (isMember(u))
            return;
        members.add(u);
        role.getGuild().getController()
                .addSingleRoleToMember(role.getGuild().getMember(u), role).complete();
    }

    public void removeMember(User u) {
        if (!isMember(u) || isOwner(u))
            return;
        members.remove(u);
        role.getGuild().getController()
                .removeSingleRoleFromMember(role.getGuild().getMember(u), role).complete();
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
        return (obj instanceof Team) && ((Team) obj).getId() == id;
    }

    @Override
    public int compareTo(Team o) {
        if (equals(o))
            return id > o.id ? 1 : -1;
        return members.size() > o.members.size() ? 1 : -1;
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
