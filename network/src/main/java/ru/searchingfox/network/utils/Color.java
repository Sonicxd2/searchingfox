package ru.searchingfox.network.utils;


public enum Color {
    FINAL(10),
    RED(50),
    ORANGE(100),
    GREEN(150),
    BLUE(200),
    WHITE(750);

    Color(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    private int distance;

}
