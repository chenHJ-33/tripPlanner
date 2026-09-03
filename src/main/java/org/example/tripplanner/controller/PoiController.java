package org.example.tripplanner.controller;

import jakarta.annotation.Resource;
import org.example.tripplanner.pojo.response.POIDetailResponse;
import org.example.tripplanner.service.MapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/poi")
public class PoiController {
    @Resource
    private MapService mapService;
    @GetMapping("/detail/{poiId}")
    public POIDetailResponse getPOIDetail(@PathVariable String poiId){
        return mapService.getPOIDetail(poiId);
    }

}
