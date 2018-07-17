package ru.searchingfox.server.datasource.realization;

import com.zaxxer.hikari.HikariConfig;
import ru.searchingfox.server.datasource.abstraction.LocalDataBaseSource;

public class SQLiteDataSource extends LocalDataBaseSource {

    public SQLiteDataSource(String file) {
        super(file);
    }

    @Override
    public void loadDriver() throws Exception {
        Class.forName("org.sqlite.SQLiteDataSource");
    }


    @Override
    public void prepareToConnect(HikariConfig config, String file) {
        config.setJdbcUrl(String.format("jdbc:sqlite:%s", file));
        config.setDriverClassName("org.sqlite.SQLiteDataSource");
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000);
        config.setIdleTimeout(45000);
        config.setMaximumPoolSize(50);
    }
}
