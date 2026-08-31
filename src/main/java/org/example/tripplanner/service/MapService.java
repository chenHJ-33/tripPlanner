package org.example.tripplanner.service;

import org.example.tripplanner.pojo.request.POISearchRequest;
import org.example.tripplanner.pojo.response.POISearchResponse;
import org.example.tripplanner.pojo.response.WeatherResponse;

public interface MapService {
    POISearchResponse searchPOI(POISearchRequest request);

    WeatherResponse getWeather(String city);
}
