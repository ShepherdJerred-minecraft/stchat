package com.shepherdjerred.stchat.mysql.objects.chatplayer;

import com.shepherdjerred.stchat.Main;
import com.shepherdjerred.stchat.mysql.ErrorManager;
import com.shepherdjerred.stchat.mysql.HikariManager;
import com.shepherdjerred.stchat.mysql.QueryHelper;
import com.shepherdjerred.stchat.objects.ChatPlayer;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChatPlayerCreator {
    static void createPlayer(@NotNull ChatPlayer player) {

        Validate.notNull(player);

        Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {

            try {

                Connection connection = HikariManager.getInstance().getConnection();
                PreparedStatement statement;

                String query = "INSERT INTO " + HikariManager.prefix + "player_listening VALUES (?, ?);";
                statement = connection.prepareStatement(query);

                statement.setString(1, String.valueOf(player.getUuid()));
                statement.setString(2, String.valueOf(Main.defaultChannel.getUuid()));

                QueryHelper.getInstance().runAsyncUpdate(connection, statement);

                connection = HikariManager.getInstance().getConnection();

                query = "INSERT INTO " + HikariManager.prefix + "player_talking VALUES (?, ?);";
                statement = connection.prepareStatement(query);

                statement.setString(1, String.valueOf(player.getUuid()));
                statement.setString(2, String.valueOf(Main.defaultChannel.getUuid()));

                QueryHelper.getInstance().runAsyncUpdate(connection, statement);

            } catch (SQLException e) {

                e.printStackTrace();
                ErrorManager.logError(e);

            }

        });

    }
}
