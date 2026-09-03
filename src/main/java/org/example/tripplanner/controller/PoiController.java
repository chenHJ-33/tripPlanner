package org.example.tripplanner.controller;

import jakarta.annotation.Resource;
import org.example.tripplanner.pojo.request.POISearchRequest;
import org.example.tripplanner.pojo.response.POIDetailResponse;
import org.example.tripplanner.pojo.response.POISearchResponse;
import org.example.tripplanner.service.MapService;
import org.example.tripplanner.service.UnsplashService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/poi")
public class PoiController {
    @Resource
    private MapService mapService;
    @Resource
    private UnsplashService unsplashService;
    @GetMapping("/detail/{poiId}")
    // 根据POI ID获取详细信息,包括图片
    public POIDetailResponse getPOIDetail(@PathVariable String poiId){
        return mapService.getPOIDetail(poiId);
    }
    @GetMapping("/search")
    // 根据关键词搜索POI
    public POISearchResponse searchPOI(String keywords,@RequestParam(defaultValue = "北京") String city){
        return mapService.searchPOI(new POISearchRequest(){{
            setCity(city);
            setKeywords(keywords);
            setCitylimit(true);
        }});
    }
    @GetMapping("/photo")
    // 根据景点名称从Unsplash获取图片
    public POIDetailResponse getAttractionPhoto(String name){
        return unsplashService.getPhotoURL(name);
    }
}
