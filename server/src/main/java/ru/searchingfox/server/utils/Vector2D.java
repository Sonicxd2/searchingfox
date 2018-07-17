package ru.searchingfox.server.utils;

import lombok.Getter;

@Getter
public class Vector2D {
    final double x;
    final double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(double x1, double y1, double x2, double y2) {
        x = x2 - x1;
        y = y2 - y1;
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize() {
        double invLength = 1 / getLength();
        return new Vector2D(x * invLength, y * invLength);
    }

    @Override
    protected Vector2D clone() {
        return new Vector2D(x, y);
    }

    public static double getAngleBetweenVectors(Vector2D firstVector, Vector2D secondVector) {
        Vector2D vector1 = firstVector.normalize();
        Vector2D vector2 = secondVector.normalize();

        double numerator = vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY();
        double denominator = vector1.getLength() * vector2.getLength();
        return Math.acos(numerator / denominator);
    }
}
