package ru.searchingfox.server.model.realization.cache;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import ru.searchingfox.server.datasource.abstraction.CacheObjectDataSource;
import ru.searchingfox.server.datasource.realization.SQLiteDataSource;
import ru.searchingfox.server.model.abstraction.Location;
import ru.searchingfox.server.model.abstraction.Player;
import ru.searchingfox.server.model.realization.PlayerRealization;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerCacheRealization extends CacheObjectDataSource<String, Player, SQLiteDataSource> {

    private static final Type LOCATION_ARRAYLIST_TYPE = new TypeToken<ArrayList<Location>>() {}.getType();
    private static final Gson GSON = new Gson();

    public PlayerCacheRealization(SQLiteDataSource databaseSource, Duration duration) {
        super(databaseSource, duration);
    }

    @Override
    protected Player loadObject(String key) throws Exception {
        @Cleanup PreparedStatement preparedStatement = databaseSource.prepareStatement(Querying.GET_OBJECT.getQuery());
        preparedStatement.setString(1, key);
        @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

        HashMap<String, ArrayList<Location>> fieldsData = new HashMap<>();

        while(resultSet.next()) {
            String field = resultSet.getString("Field");
            String found = resultSet.getString("Found");
            ArrayList<Location> locations = GSON.fromJson(found, LOCATION_ARRAYLIST_TYPE);
            fieldsData.put(field, locations);
        }

        return new PlayerRealization(key, fieldsData);
    }

    @Override
    protected void saveObject(String key, Player value) throws Exception {
        @Cleanup PreparedStatement preparedStatement = databaseSource.prepareStatement(Querying.INSERT_OBJECT.getQuery());
        preparedStatement.setString(1, key);

        for(String field : value.getActivatedFields()) {
            preparedStatement.setString(2, field);
            String foundData = GSON.toJson(value.getFoundedLocation(field), LOCATION_ARRAYLIST_TYPE);
            preparedStatement.setString(3, foundData);

            preparedStatement.execute();
        }
    }



    @Override
    protected boolean checkDatabaseSource(SQLiteDataSource databaseSource) {
        return true;
    }

    @Override
    protected void postInitializeDataSource() throws Exception {
        databaseSource.createStatement().execute(Querying.CREATE_TABLE.getQuery());
    }


    @AllArgsConstructor
    @Getter
    private enum Querying {
        CREATE_TABLE("CREATE TABLE IF NOT EXISTS \"Player\" (\"Nickname\" TEXT NOT NULL, \"Field\" TEXT NOT NULL, \"Found\" TEXT NOT NULL ON CONFLICT REPLACE, PRIMARY KEY (\"Nickname\", \"Field\"));"),
        GET_OBJECT("SELECT * FROM \"Player\" WHERE Nickname=?;"),
        INSERT_OBJECT("INSERT INTO \"Player\" VALUES (?, ?, ?);");
        private String query;
    }
}
