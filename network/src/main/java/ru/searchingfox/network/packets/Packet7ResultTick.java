package ru.searchingfox.network.packets;

import ru.searchingfox.network.utils.Color;
import ru.searchingfox.network.utils.Pair;

import java.util.List;

public class Packet7ResultTick extends Packet {
    List<Pair<Double, Color>> nearestFoxes;
    int foundFoxes;

    public Packet7ResultTick(List<Pair<Double, Color>> nearestFoxes, int foundFoxes) {
        super(7);

        this.nearestFoxes = nearestFoxes;
        this.foundFoxes = foundFoxes;
    }

    public List<Pair<Double, Color>> getNearestFoxes() {
        return nearestFoxes;
    }

    public int getFoundFoxes() {
        return foundFoxes;
    }
}
