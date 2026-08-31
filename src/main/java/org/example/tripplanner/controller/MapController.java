package org.example.tripplanner.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.tripplanner.pojo.entity.WeatherInfo;
import org.example.tripplanner.pojo.request.POISearchRequest;
import org.example.tripplanner.pojo.response.POISearchResponse;
import org.example.tripplanner.pojo.response.WeatherResponse;
import org.example.tripplanner.service.MapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/map")
@Slf4j
public class MapController {
    @Resource
    private MapService mapService;
    //根据关键词搜索POI(兴趣点)
    @GetMapping("/poi")
    public POISearchResponse searchPOI(POISearchRequest request){
        log.info("请求：/api/map/poi, 参数：{}", request);
        return mapService.searchPOI(request);
    }
    // 查询指定城市的天气信息
    @GetMapping("/weather")
    public WeatherResponse getWeather(String city){
        log.info("请求：/api/map/poi, 参数：{}",city);
        return mapService.getWeather(city);
    }
}
