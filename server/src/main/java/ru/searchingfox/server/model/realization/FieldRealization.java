package ru.searchingfox.server.model.realization;

import lombok.AllArgsConstructor;
import ru.searchingfox.server.model.abstraction.Field;
import ru.searchingfox.server.model.abstraction.Location;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FieldRealization implements Field {
    String name;
    ArrayList<Location> locations;
    long startTime;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Location> getLocations() {
        return locations;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }
}
