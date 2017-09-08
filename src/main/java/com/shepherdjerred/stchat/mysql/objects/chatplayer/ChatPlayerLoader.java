package com.shepherdjerred.stchat.mysql.objects.chatplayer;

import com.shepherdjerred.stchat.Main;
import com.shepherdjerred.stchat.files.ConfigHelper;
import com.shepherdjerred.stchat.mysql.QueryManager;
import com.shepherdjerred.stchat.objects.ChatPlayer;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.UUID;

public class ChatPlayerLoader {
    public static void loadPlayer(@NotNull UUID player) {

        Validate.notNull(player);

        QueryManager.consumer<ChatPlayer> loadPlayer = (chatPlayer) -> {

            if (ConfigHelper.debug)
                Main.getInstance().getLogger().info("Player passed to consumer");

            if (chatPlayer == null) {
                Main.getInstance().getLogger().info("Creating new player in database");
                chatPlayer = new ChatPlayer(player, Collections.singletonList(Main.defaultChannel), Main.defaultChannel);
                ChatPlayerCreator.createPlayer(chatPlayer);
            }

        };

        QueryManager.getInstance().loadPlayer(loadPlayer, player);

    }
}
