package ru.searchingfox.server.model.abstraction;

import ru.searchingfox.network.utils.Color;
import ru.searchingfox.network.utils.Pair;


import java.util.List;

public interface GameModel {
    Player getPlayer(String name) throws Exception;

    List<Pair<Double, Color>> getFoxes(Player player, Field field, Location previousLocation, Location currentLocation);

    List<String> getFields() throws Exception;

    Field getField(String name) throws Exception;
}
