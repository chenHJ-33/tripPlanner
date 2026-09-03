package org.example.tripplanner.service;

import org.example.tripplanner.pojo.request.POISearchRequest;
import org.example.tripplanner.pojo.request.RouteRequest;
import org.example.tripplanner.pojo.response.HealthResponse;
import org.example.tripplanner.pojo.response.POISearchResponse;
import org.example.tripplanner.pojo.response.RouteResponse;
import org.example.tripplanner.pojo.response.WeatherResponse;

public interface MapService {
    POISearchResponse searchPOI(POISearchRequest request);

    WeatherResponse getWeather(String city);

    RouteResponse planRoute(RouteRequest request);

    HealthResponse healthCheck();
}
