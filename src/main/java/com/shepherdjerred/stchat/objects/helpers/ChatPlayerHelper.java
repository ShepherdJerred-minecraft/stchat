package com.shepherdjerred.stchat.objects.helpers;

import com.shepherdjerred.stchat.objects.ChatPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatPlayerHelper {

    private static Map<UUID, ChatPlayer> players = new HashMap<>();

    public static ChatPlayer getChatPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public static ChatPlayer getChatPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public static void putChatPlayer(ChatPlayer chatPlayer) {
        players.put(chatPlayer.getUuid(), chatPlayer);
    }

    public static Map<UUID, ChatPlayer> getPlayers() {
        return players;
    }

}
