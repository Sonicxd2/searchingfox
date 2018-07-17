package ru.searchingfox.server.datasource.realization;

import com.zaxxer.hikari.HikariConfig;
import ru.searchingfox.server.datasource.abstraction.ExternalDataBaseSource;

public class ClickHouseDataSource extends ExternalDataBaseSource {

    public ClickHouseDataSource(String host, int port, String database, String username, String password) {
        super(host, port, database, username, password);
    }

    @Override
    public void loadDriver() throws Exception {
        Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
    }

    @Override
    public void prepareToConnect(HikariConfig hikariConfig, AuthenticationData authenticationData) {
        hikariConfig.setJdbcUrl(String.format("jdbc:clickhouse://%s:%s/%s", authenticationData.getHost(),
                authenticationData.getPort(), authenticationData.getDatabase()));
        hikariConfig.setUsername(authenticationData.getUsername());
        hikariConfig.setPassword(authenticationData.getPassword());
    }
}
