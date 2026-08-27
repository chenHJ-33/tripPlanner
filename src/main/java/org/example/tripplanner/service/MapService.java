package org.example.tripplanner.service;

import org.example.tripplanner.pojo.request.POISearchRequest;
import org.example.tripplanner.pojo.response.POISearchResponse;

public interface MapService {
    POISearchResponse searchPOI(POISearchRequest request);
}
