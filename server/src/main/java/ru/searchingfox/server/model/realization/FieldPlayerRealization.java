package ru.searchingfox.server.model.realization;

import ru.searchingfox.server.model.abstraction.Field;
import ru.searchingfox.server.model.abstraction.FieldPlayer;
import ru.searchingfox.server.model.abstraction.Location;
import ru.searchingfox.server.model.abstraction.Player;

import java.util.List;

public class FieldPlayerRealization implements FieldPlayer {
    List<Location> foundedLocations;
    List<Location> targetLocations;
    Player player;
    Field field;

    public FieldPlayerRealization(List<Location> foundedLocations, List<Location> targetLocations, Player player, Field field) {
        this.foundedLocations = foundedLocations;
        this.targetLocations = targetLocations;
        this.player = player;
        this.field = field;
    }

    @Override
    public List<Location> getFoundedLocations() {
        return foundedLocations;
    }

    @Override
    public List<Location> getTargetLocations() {
        return targetLocations;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Field getField() {
        return field;
    }
}
