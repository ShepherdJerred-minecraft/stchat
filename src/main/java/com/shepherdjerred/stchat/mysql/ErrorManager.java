package com.shepherdjerred.stchat.mysql;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class ErrorManager {

    public static void logError(Throwable aThrowable) {

        Bukkit.getServer().getScheduler().runTaskAsynchronously(com.shepherdjerred.sttowns.Main.getInstance(), () -> {

            try {

                Connection connection = HikariManager.getInstance().getConnection();
                PreparedStatement statement;

                String query = "INSERT INTO " + HikariManager.prefix + "errors (version, error_uuid, error_text) VALUES (?, ?, ?);";
                statement = connection.prepareStatement(query);

                statement.setString(1, com.shepherdjerred.sttowns.Main.getInstance().getDescription().getVersion());
                statement.setString(2, String.valueOf(UUID.randomUUID()));
                statement.setString(3, org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(aThrowable));

                QueryHelper.getInstance().runAsyncUpdate(connection, statement);

            } catch (SQLException e) {

                e.printStackTrace();

            }

        });

    }

}
