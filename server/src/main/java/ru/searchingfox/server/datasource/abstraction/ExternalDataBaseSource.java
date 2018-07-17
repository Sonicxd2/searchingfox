package ru.searchingfox.server.datasource.abstraction;

import com.zaxxer.hikari.HikariConfig;

public abstract class ExternalDataBaseSource extends DataBaseSource {
    private AuthenticationData authenticationData;

    public ExternalDataBaseSource(String host, int port, String database, String username, String password) {
        this.authenticationData = new AuthenticationData(host, port, database, username, password);
    }

    @Override
    public void prepareToConnect(HikariConfig hikariConfig) {
        prepareToConnect(hikariConfig, authenticationData);
    }

    public abstract void prepareToConnect(HikariConfig hikariConfig, AuthenticationData authenticationData);


    public static class AuthenticationData {
        private String host;
        private int port;
        private String database;
        private String username;
        private String password;

        public AuthenticationData(String host, int port, String database, String username, String password) {
            this.host = host;
            this.port = port;
            this.database = database;
            this.username = username;
            this.password = password;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public String getDatabase() {
            return database;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
