package ru.searchingfox.network.packets;

import java.util.List;

public class Packet4AnswerFields extends Packet {
    List<String> fields;

    public Packet4AnswerFields(List<String> fields) {
        super(4);
        this.fields = fields;
    }

    public List<String> getFields() {
        return fields;
    }
}
