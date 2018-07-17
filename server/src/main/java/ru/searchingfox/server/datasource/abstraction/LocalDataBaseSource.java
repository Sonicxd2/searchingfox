package ru.searchingfox.server.datasource.abstraction;

import com.zaxxer.hikari.HikariConfig;

public abstract class LocalDataBaseSource extends DataBaseSource {
    private String file;

    public LocalDataBaseSource(String file) {
        this.file = file;
    }

    @Override
    public void prepareToConnect(HikariConfig hikariConfig) {
        prepareToConnect(hikariConfig, this.file);
    }

    public abstract void prepareToConnect(HikariConfig hikariConfig, String file);
}
