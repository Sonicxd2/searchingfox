package ru.searchingfox.server.model.realization;

import ru.searchingfox.server.model.abstraction.Field;
import ru.searchingfox.server.model.abstraction.FieldPlayer;
import ru.searchingfox.server.model.abstraction.Location;
import ru.searchingfox.server.model.abstraction.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerRealization implements Player {
    String name;
    //Найденные лисы
    HashMap<String, ArrayList<Location>> fieldsData = new HashMap<>();

    public PlayerRealization(String name, HashMap<String, ArrayList<Location>> fieldsData) {
        this.name = name;
        this.fieldsData = fieldsData;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FieldPlayer getFieldPlayer(Field field) {
        List<Location> foundLocations = fieldsData.getOrDefault(field.getName(), new ArrayList<>());
        List<Location> targetLocations = new ArrayList<>();
        field.getLocations().stream().filter((loc) -> !foundLocations.contains(loc)).forEach((loc) -> targetLocations.add(loc));
        return new FieldPlayerRealization(foundLocations, targetLocations, this, field);
    }


    @Override
    public void foundFox(Field field, Location location) {
        ArrayList<Location> locations = fieldsData.get(field.getName());
        if(locations == null) {
            locations = new ArrayList<>();
            fieldsData.put(field.getName(), locations);
        }
        locations.add(location);
    }

    @Override
    public List<Location> getAvailableLocations(Field field) {
        ArrayList<Location> foundLocations = fieldsData.getOrDefault(field.getName(), new ArrayList<>());
        return field.getLocations().stream().filter(location -> !foundLocations.contains(location)).collect(Collectors.toList());
    }

    @Override
    public ArrayList<Location> getFoundedLocation(String fieldName) {
        return fieldsData.getOrDefault(fieldName, new ArrayList<>());
    }

    @Override
    public Set<String> getActivatedFields() {
        return fieldsData.keySet();
    }
}
