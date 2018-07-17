package ru.searchingfox.network.packets;

import java.util.UUID;

public class Packet6GameTick extends Packet {
    double longitude;
    double latitude;
    UUID uuid;

    public Packet6GameTick(double longitude, double latitude, UUID uuid) {
        super(6);
        this.longitude = longitude;
        this.latitude = latitude;
        this.uuid = uuid;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public UUID getUuid() {
        return uuid;
    }
}
