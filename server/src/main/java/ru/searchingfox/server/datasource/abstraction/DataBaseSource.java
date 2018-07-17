package ru.searchingfox.server.datasource.abstraction;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DataBaseSource extends DataSource {
    public abstract void loadDriver() throws Exception;

    public abstract void prepareToConnect(HikariConfig hikariConfig);

    HikariDataSource hikariDataSource;

    @Override
    public void start() {
        try {
           loadDriver();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        HikariConfig hikariConfig = new HikariConfig();
        prepareToConnect(hikariConfig);

        hikariDataSource = new HikariDataSource(hikariConfig);

        started = true;
    }

    private boolean started = false;
    public boolean isConnected() {
        return started && hikariDataSource.isRunning();
    }


    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return hikariDataSource.getConnection().prepareStatement(sql);
    }

    public Statement createStatement() throws SQLException {
        return hikariDataSource.getConnection().createStatement();
    }

    @Override
    public void close() {
        if ((hikariDataSource != null) && !(hikariDataSource.isClosed()))
            hikariDataSource.close();
    }
}
