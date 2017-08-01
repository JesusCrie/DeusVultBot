package com.jesus_crie.silverdragon.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.jesus_crie.silverdragon.SilverDragon;
import com.jesus_crie.silverdragon.manager.ThreadManager;
import net.dv8tion.jda.core.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jesus_crie.silverdragon.utils.S.f;

@JsonSerialize(using = Lobby.TeamSerializer.class)
public class Lobby implements Comparable<Lobby> {

    public static final Lobby fake = new Lobby();

    private final int id;
    private String name;
    private final Role role;
    private User owner;
    private final TextChannel channelText;
    private final VoiceChannel channelVoice;
    private List<User> members = new ArrayList<>();

    private Lobby() {
        id = -1;
        name = "fake";
        role = null;
        owner = null;
        channelText = null;
        channelVoice = null;
    }

    @JsonCreator
    private Lobby(@JsonProperty("id") int id,
                  @JsonProperty("name") String name,
                  @JsonProperty("roleId") String roleId,
                  @JsonProperty("ownerId") String ownerId,
                  @JsonProperty("channelTextId") String channelTextId,
                  @JsonProperty("channelVoiceId") String channelVoiceId) {
        this.id = id;
        this.name = name;
        role = SilverDragon.instance().getJDA().getRoleById(roleId);
        owner = SilverDragon.instance().getJDA().getUserById(ownerId);
        channelText = SilverDragon.instance().getJDA().getTextChannelById(channelTextId);
        channelVoice = SilverDragon.instance().getJDA().getVoiceChannelById(channelVoiceId);
        ThreadManager.getGeneralPool().execute(this::update);
    }

    public Lobby(int id, String name, Role role, User owner, TextChannel channelText, VoiceChannel channelVoice) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.owner = owner;
        this.channelText = channelText;
        this.channelVoice = channelVoice;
        ThreadManager.getGeneralPool().execute(this::update);
    }

    public void update() {
        role.getManager().setName(f("Lobby - %s", name)).complete();
        channelText.getManagerUpdatable().getNameField().setValue(f("team-%s", name.replace(" ", "_")))
                .getTopicField().setValue(f("Channel de la team %s", name)).update().complete();
        channelVoice.getManager().setName(f("\uD83C\uDF0F Lobby - %s", name)).complete();
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

    public void transferOwnership(User u) {
        if (!isMember(u) || isOwner(u))
            return;
        owner = u;
    }

    public boolean rename(String newName) {
        newName = newName.replaceAll("[^a-zA-Z0-9 _-]", "").trim();
        if (newName.isEmpty())
            return false;
        name = newName;
        ThreadManager.getGeneralPool().execute(this::update);
        return true;
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
        return (obj instanceof Lobby) && ((Lobby) obj).getId() == id;
    }

    @Override
    public int compareTo(Lobby o) {
        if (equals(o))
            return id > o.id ? 1 : -1;
        return members.size() > o.members.size() ? 1 : -1;
    }

    public static class TeamSerializer extends StdSerializer<Lobby> {

        public TeamSerializer() {
            this(null);
        }

        public TeamSerializer(Class<Lobby> team) {
            super(team);
        }

        @Override
        public void serialize(Lobby value, JsonGenerator gen, SerializerProvider provider) throws IOException {
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
