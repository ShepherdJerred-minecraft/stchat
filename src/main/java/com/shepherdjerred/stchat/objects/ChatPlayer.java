
package com.shepherdjerred.stchat.objects;

import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class ChatPlayer {

    private UUID uuid;
    private String name;
    private List<Channel> listening;
    private Channel talking;

    public ChatPlayer(UUID uuid, List<Channel> listening, Channel talking) {
        this.uuid = uuid;
        name = Bukkit.getOfflinePlayer(this.uuid).getName();
        this.listening = listening;
        this.talking = talking;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void addListening(Channel channel) {
        listening.add(channel);
    }

    public void removeListening(Channel channel) {
        listening.remove(channel);
    }

    public List<Channel> getListening() {
        return listening;
    }

    public List<UUID> getListeningUuids() {
        return listening.stream().map(Channel::getUuid).collect(Collectors.toList());
    }

    public Channel getTalking() {
        return talking;
    }

    public void setTalking(Channel channel) {
        talking = channel;
    }

    public boolean isListening(Channel channel) {
        return listening.contains(channel);
    }

}
