package ru.searchingfox.server.model.abstraction;

import static java.lang.Math.*;

public interface Location {
    double getLatitude();
    double getLongitude();

    void setLatitude(double latitude);
    void setLongitude(double longitude);

    LocationType getLocationType();
    void setLocationType(LocationType locationType);

    default Location toRadians() {
        if(getLocationType() == LocationType.RADIANS)
            return this;
        setLatitude(Math.toRadians(getLatitude()));
        setLongitude(Math.toRadians(getLongitude()));
        setLocationType(LocationType.RADIANS);
        return this;
    }

    default Location toDegrees() {
        if(getLocationType() == LocationType.DEGREES)
            return this;
        setLatitude(Math.toDegrees(getLatitude()));
        setLongitude(Math.toDegrees(getLongitude()));
        setLocationType(LocationType.DEGREES);
        return this;
    }

    default boolean equals(Location anotherLocation) {
        return (getLatitude() - anotherLocation.getLatitude() < 1e-6)
                && (getLongitude() - anotherLocation.getLongitude() < 1e-6);
    }

    Integer EARTH_RADIUS = 6378137;
    static double getDistance(Location location1, Location location2) {
        location1.toRadians();
        location2.toRadians();

        return 2 * EARTH_RADIUS * asin(sqrt(sqrsin((location2.getLatitude() - location1.getLatitude()) / 2)
                + cos(location1.getLatitude()) * cos(location2.getLatitude())
                * sqrsin((location2.getLongitude() - location1.getLongitude()) / 2)));
    }

    static double sqrsin(double a) {
        return sin(a) * sin(a);
    }

}
