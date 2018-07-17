package ru.searchingfox.server.model.abstraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface Player {
    String getName();

    FieldPlayer getFieldPlayer(Field field);

    void foundFox(Field field, Location location);
    List<Location> getAvailableLocations(Field field);
    default List<Location> getFoundedLocation(Field field) {
        return getFoundedLocation(field.getName());
    }

    ArrayList<Location> getFoundedLocation(String fieldName);
    Set<String> getActivatedFields();
}
