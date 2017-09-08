
package com.shepherdjerred.stchat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.shepherdjerred.stchat.commands.MainExecutor;
import com.shepherdjerred.stchat.extensions.stTitles;
import com.shepherdjerred.stchat.extensions.stTowns;
import com.shepherdjerred.stchat.files.ConfigHelper;
import com.shepherdjerred.stchat.listeners.PlayerListeners;
import com.shepherdjerred.stchat.metrics.MetricsLite;
import com.shepherdjerred.stchat.mysql.HikariManager;
import com.shepherdjerred.stchat.mysql.TableManager;
import com.shepherdjerred.stchat.objects.Channel;
import com.shepherdjerred.stchat.objects.helpers.ChannelHelper;
import com.shepherdjerred.stchat.objects.helpers.ChatPlayerHelper;
import com.shepherdjerred.stchat.redis.JedisManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;


public class Main extends JavaPlugin implements PluginMessageListener {

    public static Channel defaultChannel;
    private static Main instance;

    public Main() {
        instance = this;
    }

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        ConfigHelper.loadConfigs();

        if (ConfigHelper.redis)
            JedisManager.setupRedis();

        HikariManager.getInstance().setupPool();
        TableManager.checkTables();

        stTitles.checkDependency();
        stTowns.checkDependency();

        getCommand("stchat").setExecutor(new MainExecutor());

        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        // Start metrics
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        HikariManager.closeSource();
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

        if (!channel.equals("BungeeCord"))
            return;

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        UUID town = null;
        UUID nation = null;

        if (subchannel.contains("|town|")) {
            subchannel = subchannel.split("|town|")[0];
            town = UUID.fromString(subchannel.split("|town|")[1]);
        } else if (subchannel.contains("|nation|")) {
            subchannel = subchannel.split("|nation|")[0];
            nation = UUID.fromString(subchannel.split("|nation|")[1]);
        }

        short len = in.readShort();
        byte[] msgbytes = new byte[len];
        in.readFully(msgbytes);

        DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

        try {

            String msg = msgin.readUTF();
            if (ChannelHelper.getChannel(UUID.fromString(subchannel)) == null)
                return;

            for (Player p : getServer().getOnlinePlayers())
                if (ChatPlayerHelper.getChatPlayer(p).isListening(ChannelHelper.getChannel(UUID.fromString(subchannel)))) {

                    if (town != null) {
                        if (stTowns.enabled && !stTowns.installed && ConfigHelper.redis) {
                            if (UUID.fromString(JedisManager.getJedis().get("player_town:" + p.getUniqueId())).equals(town))
                                p.sendMessage(msg);
                        }
                    } else if (nation != null) {
                        if (stTowns.enabled && !stTowns.installed && ConfigHelper.redis) {
                            if (UUID.fromString(JedisManager.getJedis().get("town_nation:" + JedisManager.getJedis().get("player_town:" + p.getUniqueId()))).equals(nation))
                                p.sendMessage(msg);
                        } else
                            p.sendMessage(msg);

                    }
                }

            Main.getInstance().getLogger().info(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
