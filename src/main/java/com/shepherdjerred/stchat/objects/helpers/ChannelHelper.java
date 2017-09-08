package com.shepherdjerred.stchat.objects.helpers;

import com.shepherdjerred.stchat.objects.Channel;

import java.util.HashMap;
import java.util.UUID;

public class ChannelHelper {

    private static HashMap<UUID, Channel> channels = new HashMap<>();

    public static HashMap<UUID, Channel> getChannels() {
        return channels;
    }

    public static void putChannel(Channel channel) {
        channels.put(channel.getUuid(), channel);
    }

    public static Channel getChannel(UUID uuid) {
        return channels.get(uuid);
    }

    public static boolean ChannelExists(UUID uuid) {
        return channels.containsKey(uuid);
    }

}
