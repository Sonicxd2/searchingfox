package ru.searchingfox.server.model.abstraction;

import java.util.List;

public interface FieldPlayer {
    List<Location> getFoundedLocations();
    List<Location> getTargetLocations();

    Player getPlayer();
    Field getField();
}
