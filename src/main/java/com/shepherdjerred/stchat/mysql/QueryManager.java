package com.shepherdjerred.stchat.mysql;

import com.shepherdjerred.stchat.Main;
import com.shepherdjerred.stchat.extensions.stTowns;
import com.shepherdjerred.stchat.files.ConfigHelper;
import com.shepherdjerred.stchat.objects.Channel;
import com.shepherdjerred.stchat.objects.ChatPlayer;
import com.shepherdjerred.stchat.objects.helpers.ChannelHelper;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;


public class QueryManager {

    private static QueryManager instance;

    private QueryManager() {
        instance = this;
    }

    public static QueryManager getInstance() {
        if (instance == null)
            instance = new QueryManager();
        return instance;
    }

    // Load players on join
    public void loadPlayer(@NotNull consumer<ChatPlayer> consumer, @NotNull UUID player) {

        Validate.notNull(consumer);
        Validate.notNull(player);

        Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet result = null;
            ResultSet secondary = null;

            try {

                connection = HikariManager.getInstance().getConnection();

                String query = "SELECT * FROM " + HikariManager.prefix + "player_talking WHERE player_uuid = ?;";
                statement = connection.prepareStatement(query);

                statement.setString(1, String.valueOf(player));

                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info(statement.toString());

                result = statement.executeQuery();

                if (!result.next()) {
                    consumer.accept(null);
                    return;
                }

                ChatPlayer chatPlayer = new ChatPlayer(player, new ArrayList<>(), null);

                if (ChannelHelper.ChannelExists(UUID.fromString(result.getString("channel_uuid"))))
                    chatPlayer.setTalking(ChannelHelper.getChannel(UUID.fromString(result.getString("channel_uuid"))));

                // Load listening channels
                query = "SELECT * FROM " + HikariManager.prefix + "player_listening WHERE player_uuid = ?;";
                statement = connection.prepareStatement(query);

                statement.setString(1, String.valueOf(player));

                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info(statement.toString());

                secondary = statement.executeQuery();

                while (secondary.next()) {
                    if (ChannelHelper.ChannelExists(UUID.fromString(secondary.getString("channel_uuid"))))
                        chatPlayer.getListening().add(ChannelHelper.getChannel(UUID.fromString(secondary.getString("channel_uuid"))));

                    if (ConfigHelper.debug)
                        Main.getInstance().getLogger().info("Loaded listening channel " + secondary.getString("channel_uuid") + " for player " + chatPlayer.getName());
                }

                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info("Loaded player " + chatPlayer.getName());

                if (chatPlayer.getTalking() == null)
                    chatPlayer.setTalking(Main.defaultChannel);

                if (chatPlayer.getListening().isEmpty())
                    chatPlayer.getListening().add(Main.defaultChannel);

                consumer.accept(chatPlayer);

            } catch (SQLException e) {

                e.printStackTrace();
                ErrorManager.logError(e);

            } finally {

                HikariManager.getInstance().close(connection, statement, result);

            }

        });

    }

    // Load settings on start
    public void loadSettings() {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {

            connection = HikariManager.getInstance().getConnection();

            String query = "SELECT * FROM " + HikariManager.prefix + "settings WHERE name = 'default';";
            statement = connection.prepareStatement(query);

            if (ConfigHelper.debug)
                Main.getInstance().getLogger().info(statement.toString());

            result = statement.executeQuery();

            if (result.next() && ChannelHelper.getChannel(UUID.fromString(result.getString("value"))) != null) {
                Main.defaultChannel = ChannelHelper.getChannel(UUID.fromString(result.getString("value")));
                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info("Loaded default channel with uuid " + result.getString("value"));
            } else {
                Main.defaultChannel = ChannelHelper.getChannel(UUID.fromString("00000000-0000-0000-0000-000000000000"));
                Main.getInstance().getLogger().info("Couldn't load default channel! Falling back to the defaultiest default");
            }

            if (stTowns.enabled) {

                statement = connection.prepareStatement("SELECT * from " + HikariManager.getInstance().prefix + "settings WHERE name = 'town_channel';");

                result = statement.executeQuery();

                if (result.next())
                    stTowns.townChannel = ChannelHelper.getChannel(UUID.fromString(result.getString("value")));

                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info("Town channel: " + stTowns.townChannel.getName());

                statement = connection.prepareStatement("SELECT * from " + HikariManager.getInstance().prefix + "settings WHERE name = 'nation_channel';");

                result = statement.executeQuery();

                if (result.next())
                    stTowns.nationChannel = ChannelHelper.getChannel(UUID.fromString(result.getString("value")));

                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info("Nation channel: " + stTowns.nationChannel.getName());


            }

        } catch (SQLException e) {

            e.printStackTrace();
            ErrorManager.logError(e);

        } finally {

            HikariManager.getInstance().close(connection, statement, result);

        }

    }


    public void loadChannels() {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ResultSet secondary = null;

        try {

            connection = HikariManager.getInstance().getConnection();

            String query = "SELECT * FROM " + HikariManager.prefix + "channels;";
            statement = connection.prepareStatement(query);

            if (ConfigHelper.debug)
                Main.getInstance().getLogger().info(statement.toString());

            result = statement.executeQuery();

            while (result.next()) {

                Channel channel = new Channel(UUID.fromString(result.getString("channel_uuid")), result.getString("name"), result.getString("format"), result.getString("permission"));

                // Load flags
                EnumMap<Channel.Flag, Integer> flags = new EnumMap<>(Channel.Flag.class);

                query = "SELECT * FROM " + HikariManager.prefix + "channel_flags WHERE channel_uuid = ?";
                statement = connection.prepareStatement(query);

                statement.setString(1, result.getString("channel_uuid"));

                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info(statement.toString());

                secondary = statement.executeQuery();

                while (secondary.next()) {
                    flags.put(Channel.Flag.valueOf(secondary.getString("flag")), secondary.getInt("value"));

                    if (ConfigHelper.debug)
                        Main.getInstance().getLogger().info("Loaded flag " + secondary.getString("flag") + " with value " + secondary.getInt("value") + " for nation " + channel.getName());
                }

                channel.setFlags(flags);

                // Load aliases
                List<String> aliases = new ArrayList<>();

                query = "SELECT * FROM " + HikariManager.prefix + "channel_aliases WHERE channel_uuid = ?";
                statement = connection.prepareStatement(query);

                statement.setString(1, result.getString("channel_uuid"));

                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info(statement.toString());

                secondary = statement.executeQuery();

                while (secondary.next()) {
                    aliases.add(result.getString("alias"));

                    if (ConfigHelper.debug)
                        Main.getInstance().getLogger().info("Loaded alias " + secondary.getInt("alias") + " for channel " + channel.getName());
                }

                channel.setAliases(aliases);

                // Load ranks
                HashMap<UUID, Channel.Rank> ranks = new HashMap<>();

                query = "SELECT * FROM " + HikariManager.prefix + "channel_ranks WHERE channel_uuid = ?";
                statement = connection.prepareStatement(query);

                statement.setString(1, result.getString("channel_uuid"));

                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info(statement.toString());

                secondary = statement.executeQuery();

                while (secondary.next()) {
                    ranks.put(UUID.fromString(result.getString("player_uuid")), Channel.Rank.valueOf(result.getString("rank")));

                    if (ConfigHelper.debug)
                        Main.getInstance().getLogger().info("Loaded rank " + secondary.getInt("rank") + " for player" + result.getString("player_uuid") + "in channel " + channel.getName());
                }

                channel.setAllMembers(ranks);

                // Load statuses
                HashMap<UUID, Channel.Status> status = new HashMap<>();

                query = "SELECT * FROM " + HikariManager.prefix + "channel_statuses WHERE channel_uuid = ?";
                statement = connection.prepareStatement(query);

                statement.setString(1, result.getString("channel_uuid"));

                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info(statement.toString());

                secondary = statement.executeQuery();

                while (secondary.next()) {
                    status.put(UUID.fromString(result.getString("player_uuid")), Channel.Status.valueOf(result.getString("status")));

                    if (ConfigHelper.debug)
                        Main.getInstance().getLogger().info("Loaded status " + secondary.getInt("status") + " for player" + result.getString("player_uuid") + "in channel " + channel.getName());
                }

                channel.setAllStatus(status);

                channel.setDefaultFlags();

                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info("Loaded channel " + channel.getName() + " with the uuid " + channel.getUuid());

            }

        } catch (SQLException e) {

            e.printStackTrace();
            ErrorManager.logError(e);

        } finally {

            HikariManager.getInstance().close(connection, statement, result);
            HikariManager.getInstance().close(null, null, secondary);

        }

    }

    // MySQL check if tables exist
    void checkTables(consumer<Boolean> consumer) {

        Connection connection = null;
        ResultSet result = null;
        List<String> tables = new ArrayList<>();

        try {

            connection = HikariManager.getInstance().getConnection();

            DatabaseMetaData dbm = connection.getMetaData();
            String[] types = {"TABLE"};

            result = dbm.getTables(null, null, "%", types);

            while (result.next()) {
                if (ConfigHelper.debug)
                    Main.getInstance().getLogger().info("Table exists: " + result.getString("TABLE_NAME"));
                tables.add(result.getString("TABLE_NAME"));
            }

            if (consumer != null)
                consumer.accept(tables.containsAll(Arrays.asList(
                        HikariManager.prefix + "player_listening",
                        HikariManager.prefix + "player_talking",
                        HikariManager.prefix + "channels",
                        HikariManager.prefix + "channel_ranks",
                        HikariManager.prefix + "channel_aliases",
                        HikariManager.prefix + "channel_statuses",
                        HikariManager.prefix + "channel_flags",
                        HikariManager.prefix + "settings"
                )));

        } catch (SQLException e) {

            e.printStackTrace();
            ErrorManager.logError(e);

        } finally {

            HikariManager.getInstance().close(connection, null, result);

        }

    }


    public interface consumer<T> {

        void accept(T result);

    }

}
