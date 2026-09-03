package org.example.tripplanner.controller;

import jakarta.annotation.Resource;
import org.example.tripplanner.pojo.request.POISearchRequest;
import org.example.tripplanner.pojo.response.POIDetailResponse;
import org.example.tripplanner.pojo.response.POISearchResponse;
import org.example.tripplanner.service.MapService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/poi")
public class PoiController {
    @Resource
    private MapService mapService;
    @GetMapping("/detail/{poiId}")
    public POIDetailResponse getPOIDetail(@PathVariable String poiId){
        return mapService.getPOIDetail(poiId);
    }
    @GetMapping("/search")
    public POISearchResponse searchPOI(String keywords,@RequestParam(defaultValue = "北京") String city){
        return mapService.searchPOI(new POISearchRequest(){{
            setCity(city);
            setKeywords(keywords);
            setCitylimit(true);
        }});
    }
}
