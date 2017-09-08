
package com.shepherdjerred.stchat.mysql;

import com.shepherdjerred.stchat.Main;
import com.shepherdjerred.stchat.files.FileManager;

import java.util.ArrayList;
import java.util.List;


public class TableManager {

    public static void checkTables() {

        QueryManager.consumer<Boolean> checkTables = result -> {

            if (result) {

                Main.getInstance().getLogger().info("All tables exist, loading data");

                updateTables();

            } else {

                Main.getInstance().getLogger().info("Tables don't exist, creating them now");

                List<String> tables = new ArrayList<>();

                tables.add("CREATE TABLE IF NOT EXISTS " + HikariManager.getInstance().prefix +
                        "player_listening (player_uuid CHAR(36), channel_uuid CHAR(36), UNIQUE (player_uuid, channel_uuid));");
                tables.add("CREATE TABLE IF NOT EXISTS " + HikariManager.getInstance().prefix +
                        "player_talking (player_uuid CHAR(36), channel_uuid CHAR(36), UNIQUE (player_uuid));");

                tables.add("CREATE TABLE IF NOT EXISTS " + HikariManager.getInstance().prefix +
                        "channels (channel_uuid CHAR(36), name VARCHAR(16), format TEXT, permission TEXT, UNIQUE (channel_uuid), UNIQUE (name));");
                tables.add("CREATE TABLE IF NOT EXISTS " + HikariManager.getInstance().prefix +
                        "channel_ranks (channel_uuid CHAR(36), player_uuid CHAR(36), rank VARCHAR(60), UNIQUE (channel_uuid, player_uuid));");
                tables.add("CREATE TABLE IF NOT EXISTS " + HikariManager.getInstance().prefix +
                        "channel_aliases (channel_uuid CHAR(36), alias VARCHAR(16), UNIQUE (alias));");
                tables.add("CREATE TABLE IF NOT EXISTS " + HikariManager.getInstance().prefix +
                        "channel_statuses (channel_uuid CHAR(36), player_uuid CHAR(36), status SMALLINT, UNIQUE (channel_uuid, player_uuid));");
                tables.add("CREATE TABLE IF NOT EXISTS " + HikariManager.getInstance().prefix +
                        "channel_flags (channel_uuid CHAR(36), flag VARCHAR(60), value INT, UNIQUE (channel_uuid, flag));");

                tables.add("CREATE TABLE IF NOT EXISTS " + HikariManager.getInstance().prefix +
                        "settings (name VARCHAR(30), value VARCHAR(255), UNIQUE (name));");

                tables.add("CREATE TABLE IF NOT EXISTS " + HikariManager.prefix + "errors " +
                        "(error_date DATETIME DEFAULT CURRENT_TIMESTAMP, error_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, version TEXT, error_uuid CHAR(36), error_text LONGTEXT, UNIQUE (error_uuid));");

                tables.add("INSERT INTO " + HikariManager.getInstance().prefix
                        + "channels VALUES ('00000000-0000-0000-0000-000000000000', 'Global', '&8[&2G&8][%prefix%%player%&8]: &7%message%', '');");
                tables.add("INSERT INTO " + HikariManager.getInstance().prefix + "settings VALUES ('default', '00000000-0000-0000-0000-000000000000');");

                QueryHelper.getInstance().massRunUpdates(tables);

            }

            QueryManager.getInstance().loadChannels();
            QueryManager.getInstance().loadSettings();

        };

        QueryManager.getInstance().checkTables(checkTables);

    }

    private static void updateTables() {

        int tableVersion = FileManager.getInstance().storage.getInt("mysqlTableVersion");
        List<String> updates = new ArrayList<>();

        switch (tableVersion) {

            case 1:

        }

        if (!updates.isEmpty()) {

            QueryHelper.getInstance().massRunUpdates(updates);

            Main.getInstance().getLogger().info("MySQL tables updated to version 2");

            FileManager.getInstance().storage.set("mysqlTableVersion", 2);

        }

    }

}
