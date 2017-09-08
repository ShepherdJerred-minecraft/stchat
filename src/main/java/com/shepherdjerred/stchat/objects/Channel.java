package com.shepherdjerred.stchat.objects;

import com.shepherdjerred.stchat.objects.helpers.ChannelHelper;

import java.util.*;


public class Channel {

    private UUID uuid;
    private String name;
    private HashMap<UUID, Rank> members;
    private String format;
    private String permission;
    private List<String> aliases;
    private HashMap<UUID, Status> status;
    private EnumMap<Flag, Integer> flags;

    public Channel(UUID uuid, String name, String format, String permission) {
        this.uuid = uuid;
        this.name = name;
        this.format = format;
        this.permission = permission;
        members = new HashMap<>();
        aliases = new ArrayList<>();
        status = new HashMap<>();
        flags = new EnumMap<>(Flag.class);
        ChannelHelper.putChannel(this);
    }

    // TODO Don't do this
    public void setDefaultFlags() {
        flags.put(Flag.WHITELIST, 0);
        flags.put(Flag.BLACKLIST, 0);
        flags.put(Flag.PERMISSION, 1);
        flags.put(Flag.BUNGEECORD, 0);
        flags.put(Flag.COLOR_CODES, 1);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<UUID, Rank> getMembers() {
        return members;
    }

    public void setAllMembers(HashMap<UUID, Rank> members) {
        this.members = members;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public HashMap<UUID, Status> getStatus() {
        return status;
    }

    public void setAllStatus(HashMap<UUID, Status> status) {
        this.status = status;
    }

    public EnumMap<Flag, Integer> getFlags() {
        return flags;
    }

    public void setFlags(EnumMap<Flag, Integer> flags) {
        this.flags = flags;
    }

    public Integer getFlag(Flag flag) {
        return flags.get(flag);
    }

    public enum Rank {
        OWNER, MODERATOR
    }

    public enum Flag {
        WHITELIST, BLACKLIST, PERMISSION, BUNGEECORD, COLOR_CODES
    }

    public enum Status {
        WHITELISTED, BLACKLISTED
    }

}