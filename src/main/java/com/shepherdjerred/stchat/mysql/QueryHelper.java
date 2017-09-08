package com.shepherdjerred.stchat.mysql;

import com.shepherdjerred.stchat.Main;
import com.shepherdjerred.stchat.files.ConfigHelper;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class QueryHelper {

    private static QueryHelper instance;

    private QueryHelper() {
        instance = this;
    }

    public static QueryHelper getInstance() {
        if (instance == null) {
            instance = new QueryHelper();
        }
        return instance;
    }

    public void runAsyncUpdate(@NotNull Connection connection, @NotNull PreparedStatement input) {
        Validate.notNull(connection);
        Validate.notNull(input);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> runUpdate(connection, input));
    }

    public void runUpdate(@NotNull Connection connection, @NotNull PreparedStatement input) {
        Validate.notNull(connection);
        Validate.notNull(input);
        try {
            if (ConfigHelper.debug)
                Main.getInstance().getLogger().info(input.toString());
            input.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorManager.logError(e);
        } finally {
            HikariManager.getInstance().close(connection, input, null);
        }
    }

    public void massRunUpdates(@NotNull List<String> updates) {
        Validate.notNull(updates);
        Validate.notEmpty(updates);
        try {
            for (String query : updates) {
                Connection connection = HikariManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                runUpdate(connection, statement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorManager.logError(e);
        }
    }

}
