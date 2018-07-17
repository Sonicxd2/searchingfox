package ru.searchingfox.server.model.realization.cache;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import ru.searchingfox.server.datasource.abstraction.CacheObjectDataSource;
import ru.searchingfox.server.datasource.realization.SQLiteDataSource;
import ru.searchingfox.server.model.abstraction.Field;
import ru.searchingfox.server.model.abstraction.Location;
import ru.searchingfox.server.model.realization.FieldRealization;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;

public class FieldCacheRealization extends CacheObjectDataSource<String, Field, SQLiteDataSource> {
    private static final Gson GSON = new Gson();

    public FieldCacheRealization(SQLiteDataSource databaseSource, Duration duration) {
        super(databaseSource, duration);
    }

    @Override
    protected Field loadObject(String key) throws Exception {
        @Cleanup PreparedStatement preparedStatement = databaseSource.prepareStatement(Querying.SELECT_FROM_FIELD.getQuery());
        preparedStatement.setString(1, key);
        @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

        if(!resultSet.next()) {
            return new FieldRealization(key, new ArrayList<>(), System.currentTimeMillis());
        }
        long startedTime = resultSet.getInt("Time");

        resultSet.close();
        preparedStatement.close();

        preparedStatement = databaseSource.prepareStatement(Querying.SELECT_FROM_FIELD_LOCATIONS.getQuery());
        resultSet = preparedStatement.executeQuery();

        ArrayList<Location> locations = new ArrayList<>();
        while(resultSet.next()) {
            locations.add(GSON.fromJson(resultSet.getString("Location"), Location.class));
        }

        return new FieldRealization(key, locations, startedTime);
    }

    @Override
    protected void saveObject(String key, Field value) throws Exception {
        @Cleanup PreparedStatement preparedStatement = databaseSource.prepareStatement(Querying.INSERT_INTO_FIELD.getQuery());
        preparedStatement.setString(1, key);
        preparedStatement.setInt(2, (int) value.getStartTime());
        preparedStatement.execute();
        preparedStatement.close();

        preparedStatement = databaseSource.prepareStatement(Querying.INSERT_INTO_FIELD_LOCATIONS.getQuery());
        preparedStatement.setString(1, key);
        for(Location location : value.getLocations()) {
            preparedStatement.setString(2, GSON.toJson(location));
            preparedStatement.execute();
        }
    }

    @Override
    protected boolean checkDatabaseSource(SQLiteDataSource databaseSource) {
        return true;
    }

    @Override
    protected void postInitializeDataSource() throws Exception {
        databaseSource.createStatement().execute(Querying.CREATE_TABLE_FIELD.getQuery());
        databaseSource.createStatement().execute(Querying.CREATE_TABLE_FIELD_LOCATIONS.getQuery());

    }

    @Getter
    @AllArgsConstructor
    private enum Querying {
        CREATE_TABLE_FIELD("CREATE TABLE IF NOT EXISTS \"Field\" (\"Name\" TEXT NOT NULL, \"Time\" integer, PRIMARY KEY (\"Name\") ON CONFLICT REPLACE);"),
        INSERT_INTO_FIELD("INSERT INTO \"Field\" VALUES (?, ?);"),
        SELECT_FROM_FIELD("SELECT * FROM \"Field\" WHERE Name=?;"),
        CREATE_TABLE_FIELD_LOCATIONS("CREATE TABLE IF NOT EXISTS \"FieldLocations\" (\"Name\" TEXT NOT NULL ON CONFLICT REPLACE, \"Location\" TEXT NOT NULL, PRIMARY KEY (\"Name\", \"Location\"));"),
        INSERT_INTO_FIELD_LOCATIONS("INSERT INTO \"FieldLocations\" VALUES (?, ?);"),
        SELECT_FROM_FIELD_LOCATIONS("SELECT * FROM FieldLocations WHERE Name = ?;");


        private String query;
    }
}
