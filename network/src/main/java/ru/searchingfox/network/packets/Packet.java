package ru.searchingfox.network.packets;

import java.io.Serializable;

public class Packet implements Serializable {
    final int id;

    public Packet(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
