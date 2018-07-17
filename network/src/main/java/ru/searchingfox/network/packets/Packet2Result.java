package ru.searchingfox.network.packets;

import java.util.UUID;

public class Packet2Result extends Packet {
    String resultMessage;
    UUID loginUUID;

    public Packet2Result(String resultMessage, UUID loginUUID) {
        super(2);
        this.resultMessage = resultMessage;
        this.loginUUID = loginUUID;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public UUID getLoginUUID() {
        return loginUUID;
    }
}
