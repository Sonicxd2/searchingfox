package ru.searchingfox.server.model.realization.cache;

import lombok.Cleanup;
import lombok.Getter;
import ru.searchingfox.server.datasource.abstraction.CacheObjectDataSource;
import ru.searchingfox.server.datasource.realization.SQLiteDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;

public class AuthCacheRealization extends CacheObjectDataSource<String, String, SQLiteDataSource> {


    public AuthCacheRealization(SQLiteDataSource databaseSource, Duration duration) {
        super(databaseSource, duration);
    }

    @Override
    protected String loadObject(String key) throws Exception {
        PreparedStatement preparedStatement = databaseSource.prepareStatement(Querying.GET_OBJECT.getQuery());
        preparedStatement.setString(1, key);
        @Cleanup ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()) {
            return resultSet.getString("Password");
        }
        return null;
    }

    @Override
    protected void saveObject(String key, String value) throws Exception {
        PreparedStatement preparedStatement = databaseSource.prepareStatement(Querying.SAVE_OBJECT.getQuery());
        preparedStatement.setString(1, key);
        preparedStatement.setString(2, value);
        preparedStatement.execute();
    }

    @Override
    protected boolean checkDatabaseSource(SQLiteDataSource databaseSource) {
        return true;
    }

    @Override
    protected void postInitializeDataSource() throws Exception {
        databaseSource.createStatement().execute(Querying.CREATE_TABLE.getQuery());
    }

    private enum Querying {
        CREATE_TABLE("CREATE TABLE IF NOT EXISTS \"Auth\"(\"Nickname\" TEXT NOT NULL, \"Password\" text, PRIMARY KEY (\"Nickname\"));"),
        GET_OBJECT("SELECT * FROM Auth WHERE Nickname=\"?\";"),
        SAVE_OBJECT("INSERT INTO Auth VALUES(?, ?);");

        @Getter
        String query;

        Querying(String query) {
            this.query = query;
        }

        @Override
        public String toString() {
            return query;
        }
    }
}
