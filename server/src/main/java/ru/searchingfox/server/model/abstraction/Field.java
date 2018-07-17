package ru.searchingfox.server.model.abstraction;

import java.util.List;

public interface Field {
    String getName();

    /**
     * Тут нужна оговорка. БД должна явно гарантировать неизменность данных после начала игры.
     * @return
     */
    List<Location> getLocations();
    long getStartTime();
}
