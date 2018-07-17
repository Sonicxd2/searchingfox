package ru.searchingfox.network.example;

import ru.searchingfox.network.packets.Packet;

public class Packet0Message extends Packet {
    String message;

    public Packet0Message(String message) {
        super(0);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
