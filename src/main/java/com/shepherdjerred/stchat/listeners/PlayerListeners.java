
package com.shepherdjerred.stchat.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.shepherdjerred.stchat.Main;
import com.shepherdjerred.stchat.extensions.stTowns;
import com.shepherdjerred.stchat.messages.MessageHelper;
import com.shepherdjerred.stchat.mysql.objects.chatplayer.ChatPlayerLoader;
import com.shepherdjerred.stchat.objects.Channel;
import com.shepherdjerred.stchat.objects.Channel.Flag;
import com.shepherdjerred.stchat.objects.ChatPlayer;
import com.shepherdjerred.stchat.objects.helpers.ChatPlayerHelper;
import com.shepherdjerred.stchat.redis.JedisManager;
import com.shepherdjerred.sttitles.objects.TitlePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class PlayerListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerLoginEvent event) {

        ChatPlayer chatPlayer = new ChatPlayer(event.getPlayer().getUniqueId(), Arrays.asList(Main.defaultChannel), Main.defaultChannel);

        ChatPlayerHelper.putChatPlayer(chatPlayer);

        ChatPlayerLoader.loadPlayer(event.getPlayer().getUniqueId());

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        ChatPlayerHelper.getPlayers().remove(event.getPlayer().getUniqueId());

    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        Channel channel = ChatPlayerHelper.getChatPlayer(player).getTalking();

        event.setFormat(MessageHelper.colorString(channel.getFormat().replace("%player%", player.getName())
                .replace("%prefix%", TitlePlayer.getTitlePlayer(event.getPlayer()).getTitle().getContent()).replace("%message%", "%2$s")));

        Bukkit.getOnlinePlayers().stream().filter(p -> !ChatPlayerHelper.getChatPlayer(p).isListening(channel)).forEach(p -> event.getRecipients().remove(p));

        if (channel.getFlag(Flag.BUNGEECORD) == 1) {

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ONLINE");

            // TODO Check if player has town
            // TODO Check if town has nation
            if (stTowns.enabled) {
                if (stTowns.townChannel == channel)
                    out.writeUTF(String.valueOf(channel.getUuid()) + "|town|" + JedisManager.getJedis().get("player_town:" + player.getUniqueId()));
                if (stTowns.nationChannel == channel)
                    out.writeUTF(String.valueOf(channel.getUuid()) + "|nation|" + JedisManager.getJedis().get("town_nation:" + JedisManager.getJedis().get("player_town:" + player.getUniqueId())));
                else
                    out.writeUTF(String.valueOf(channel.getUuid()));
            } else
                out.writeUTF(String.valueOf(channel.getUuid()));

            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);

            try {

                msgout.writeUTF(event.getFormat().replace("%2$s", event.getMessage()));
                msgout.writeShort(123);

            } catch (IOException e) {

                e.printStackTrace();

            }

            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());

            player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());

        }

    }

}
