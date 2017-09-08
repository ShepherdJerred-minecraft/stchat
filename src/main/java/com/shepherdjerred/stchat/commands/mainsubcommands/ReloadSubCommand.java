package com.shepherdjerred.stchat.commands.mainsubcommands;

import com.shepherdjerred.stchat.Main;
import com.shepherdjerred.stchat.files.ConfigHelper;
import com.shepherdjerred.stchat.messages.commands.GenericMessages;
import com.shepherdjerred.stchat.messages.commands.MainCommandMessages;
import com.shepherdjerred.stchat.mysql.HikariManager;
import com.shepherdjerred.stchat.mysql.TableManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadSubCommand {

    public static void Executor(CommandSender sender, String[] args) {

        if (!sender.hasPermission("stTowns.reload")) {
            sender.sendMessage(GenericMessages.getNoPermsMessage());
            return;
        }

        Main.getInstance().reloadConfig();
        ConfigHelper.loadConfigs();

        HikariManager.getInstance().setupPool();
        TableManager.checkTables();

        for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
            // TODO Reload player data
        }

        sender.sendMessage(MainCommandMessages.getReloadMessage());

    }

}
