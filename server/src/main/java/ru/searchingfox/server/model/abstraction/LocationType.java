package ru.searchingfox.server.model.abstraction;

public enum LocationType {
    RADIANS {
        @Override
        public String toString() {
            return "Radians";
        }
    }, DEGREES {
        @Override
        public String toString() {
            return "Degrees";
        }
    }
}
