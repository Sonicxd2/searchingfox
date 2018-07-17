package ru.searchingfox.server.model.realization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.searchingfox.server.model.abstraction.Location;
import ru.searchingfox.server.model.abstraction.LocationType;

@Getter
@Setter
@AllArgsConstructor
public class LocationRealization implements Location {
    double latitude;
    double longitude;
    LocationType locationType;
}
