package com.shepherdjerred.stchat.messages;

import com.shepherdjerred.stchat.Main;
import com.shepherdjerred.stchat.files.FileManager;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class MessageHelper {

    @NotNull
    public static String colorString(@NotNull String input) {
        Validate.notNull(input);
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    @NotNull
    public static String colorConfigString(@NotNull String input) {
        Validate.notNull(input);
        if (Main.getInstance().getConfig().getString(input) == null)
            return "MESSAGE NOT FOUND: " + input;
        return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString(input));
    }

    @NotNull
    public static String colorMessagesString(@NotNull String input) {
        Validate.notNull(input);
        if (FileManager.getInstance().messages.getString(input) == null)
            return "MESSAGE NOT FOUND: " + input;
        return ChatColor.translateAlternateColorCodes('&', FileManager.getInstance().messages.getString(input));
    }

    @NotNull
    public static String getMessagePrefix() {
        return colorMessagesString("prefix");
    }


}
