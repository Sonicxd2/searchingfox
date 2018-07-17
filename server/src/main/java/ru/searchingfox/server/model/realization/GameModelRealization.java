package ru.searchingfox.server.model.realization;

import ru.searchingfox.network.utils.Color;
import ru.searchingfox.network.utils.Pair;
import ru.searchingfox.server.datasource.realization.SQLiteDataSource;
import ru.searchingfox.server.model.abstraction.*;
import ru.searchingfox.server.model.realization.cache.FieldCacheRealization;
import ru.searchingfox.server.model.realization.cache.PlayerCacheRealization;
import ru.searchingfox.server.utils.Vector2D;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class GameModelRealization implements GameModel {
    private FieldCacheRealization fieldCacheRealization;
    private PlayerCacheRealization playerCacheRealization;
    private SQLiteDataSource sqLiteDataSource;

    public GameModelRealization(SQLiteDataSource sqLiteDataSource) {
        this.fieldCacheRealization = new FieldCacheRealization(sqLiteDataSource, Duration.ofMinutes(5));
        this.playerCacheRealization = new PlayerCacheRealization(sqLiteDataSource, Duration.ofMinutes(5));

        try {
            this.fieldCacheRealization.start();
            this.playerCacheRealization.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.sqLiteDataSource = sqLiteDataSource;
    }

    public GameModelRealization(SQLiteDataSource sqLiteDataSource, CacheDatabasePool cacheDatabasePool) {
        this(sqLiteDataSource);
        cacheDatabasePool.addCache(fieldCacheRealization);
        cacheDatabasePool.addCache(playerCacheRealization);
    }

    public Player getPlayer(String name) throws Exception {
        return playerCacheRealization.getObject(name);
    }


    //TODO: Change search method to Binary search
    public List<Pair<Double, Color>> getFoxes(Player player, Field field, Location previousLocation, Location currentLocation) {
        synchronized (player) {
            FieldPlayer fieldPlayer = player.getFieldPlayer(field);
            ArrayList<Pair<Double, Color>> result = new ArrayList<>();
            for (Location location : fieldPlayer.getTargetLocations()) {
                double distance = Location.getDistance(currentLocation, location);
                int i = 0;
                while ((i + 1 < Color.values().length) && ((Color.values()[i].getDistance() <= distance))) i++;
                switch (Color.values()[i]) {
                    case WHITE:
                        break;
                    case FINAL: {
                        player.foundFox(field, location);
                        break;
                    }
                    default:
                        result.add(new Pair<>(Vector2D.getAngleBetweenVectors(
                                new Vector2D(previousLocation.getLatitude(), previousLocation.getLongitude(), currentLocation.getLatitude(), currentLocation.getLongitude()),
                                new Vector2D(currentLocation.getLatitude(), currentLocation.getLongitude(), location.getLatitude(), location.getLongitude())
                        ), Color.values()[i]));
                }
            }
            return result;
        }
    }

    @Override
    public List<String> getFields() throws Exception {
        ArrayList<String> fields = new ArrayList<>();

        PreparedStatement preparedStatement = sqLiteDataSource.prepareStatement("SELECT Name FROM \"Field\";");
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()) {
            fields.add(resultSet.getString("Name"));
        }

        return fields;
    }

    public Field getField(String name) throws Exception {
        return fieldCacheRealization.getObject(name);
    }

}
