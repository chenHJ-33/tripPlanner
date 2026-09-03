package org.example.tripplanner.service;

import org.example.tripplanner.pojo.request.POISearchRequest;
import org.example.tripplanner.pojo.request.RouteRequest;
import org.example.tripplanner.pojo.response.*;

public interface MapService {
    POISearchResponse searchPOI(POISearchRequest request);

    WeatherResponse getWeather(String city);

    RouteResponse planRoute(RouteRequest request);

    HealthResponse healthCheck();

    POIDetailResponse getPOIDetail(String poiId);
}
