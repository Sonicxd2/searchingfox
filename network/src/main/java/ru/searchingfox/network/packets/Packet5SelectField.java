package ru.searchingfox.network.packets;

import java.util.UUID;

public class Packet5SelectField extends Packet {
    UUID uuid;
    String selectedField;

    public Packet5SelectField(String selectedField, UUID playerUUID) {
        super(5);
        this.selectedField = selectedField;
        this.uuid = playerUUID;
    }

    public String getSelectedField() {
        return selectedField;
    }

    public UUID getUuid() {
        return uuid;
    }
}
