package com.shepherdjerred.stchat.mysql;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HikariManager {


    public static String host, port, database, username, password, prefix;
    public static int min, max;
    public static long connectionTimeout, idleTimeout;

    private static HikariManager instance;
    private static HikariDataSource dataSource = new HikariDataSource();

    private HikariManager() {
        instance = this;
    }

    public static HikariManager getInstance() {
        if (instance == null)
            instance = new HikariManager();
        return instance;
    }

    public static void closeSource() {
        dataSource.close();
    }

    public void setupPool() {
        dataSource.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMinimumIdle(min);
        dataSource.setMaximumPoolSize(max);
        dataSource.setConnectionTimeout(connectionTimeout);
        dataSource.setIdleTimeout(idleTimeout);
    }

    @NotNull
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    void close(Connection conn, PreparedStatement ps, ResultSet res) {
        try {
            if (conn != null)
                conn.close();
            if (ps != null)
                ps.close();
            if (res != null)
                res.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ErrorManager.logError(e);
        }
    }


}
