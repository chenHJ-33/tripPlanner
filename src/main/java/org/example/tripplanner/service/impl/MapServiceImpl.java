package org.example.tripplanner.service.impl;

import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.tripplanner.pojo.entity.POIInfo;
import org.example.tripplanner.pojo.entity.RouteInfo;
import org.example.tripplanner.pojo.entity.WeatherInfo;
import org.example.tripplanner.pojo.request.POISearchRequest;
import org.example.tripplanner.pojo.request.RouteRequest;
import org.example.tripplanner.pojo.response.*;
import org.example.tripplanner.service.MapService;
import org.example.tripplanner.tool.AmapPoiDetailParser;
import org.example.tripplanner.tool.AmapPoiParser;
import org.example.tripplanner.tool.AmapRouteParser;
import org.example.tripplanner.tool.AmapWeatherParser;
import org.example.tripplanner.tool.MCPTool;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地图服务实现：通过高德地图 MCP 工具查询
 */
@Service
@Slf4j
public class MapServiceImpl implements MapService {

    /** 高德 MCP 文本搜索工具名 */
    private static final String TOOL_TEXT_SEARCH = "maps_text_search";
    private static final String TOOL_WEATHER="maps_weather";
    private static final String MAPS_SEARCH_DETAIL="maps_search_detail";

    @Resource
    private MCPTool mcpTool;

    @Override
    public POISearchResponse searchPOI(POISearchRequest request) {
        POISearchResponse response = new POISearchResponse();
        if (request == null || isBlank(request.getKeywords()) || isBlank(request.getCity())) {
            response.setSuccess(false);
            response.setMessage("keywords和city为必填参数");
            return response;
        }
        try {
            Map<String, Object> args = new HashMap<>();
            args.put("keywords", request.getKeywords());
            args.put("city", request.getCity());
            args.put("citylimit", String.valueOf(Boolean.TRUE.equals(request.getCitylimit())));

            String text = mcpTool.callTool(TOOL_TEXT_SEARCH, args);
            log.info("POI搜索结果: {}", abbreviate(text));

            List<POIInfo> pois = AmapPoiParser.parse(text);
            response.setSuccess(true);
            response.setMessage("POI搜索成功");
            response.setData(pois);
        } catch (Exception e) {
            log.error("POI搜索失败: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage("POI搜索失败: " + e.getMessage());
        }
        return response;
    }

    @Override
    public WeatherResponse getWeather(String city) {
        WeatherResponse wr=new WeatherResponse();
        if (isBlank(city)){
            wr.setMessage("city不能为空");
            wr.setSuccess(false);
            return wr;
        }
        try {
            Map<String,Object> args=new HashMap<>();
            args.put("city",city);
            String text = mcpTool.callTool(TOOL_WEATHER, args);
            log.info("天气搜索结果：{}",abbreviate(text));
            List<WeatherInfo> forecasts = AmapWeatherParser.parse(text);
            wr.setSuccess(true);
            wr.setMessage("天气查询成功");
            wr.setData(forecasts);
            return wr;
        }catch (Exception e){
            log.error("搜索天气失败: {}",e.getMessage(),e);
            wr.setSuccess(false);
            wr.setMessage("搜索天气失败"+e.getMessage());
        }
        return wr;
    }

    @Override
    public RouteResponse planRoute(RouteRequest request) {
        RouteResponse rr=new RouteResponse();
        if (request == null || request.getOriginAddress() == null || request.getDestinationAddress() == null) {
            rr.setMessage("起点地址与终点地址不能为空");
            rr.setSuccess(false);
            return rr;
        }
        try {
            Map<String,String> toolMap=new HashMap<>();
            toolMap.put("walking","maps_direction_walking_by_address");
            toolMap.put("driving","maps_direction_driving_by_address");
            toolMap.put("transit","maps_direction_transit_integrated_by_address");
            String routeType=toolMap.containsKey(request.getRouteType()) ? request.getRouteType() : "walking";
            String toolName=toolMap.get(routeType);
            Map<String,Object> args=new HashMap<>();
            args.put("origin_address",request.getOriginAddress());
            args.put("destination_address",request.getDestinationAddress());
            if (request.getOriginCity() != null) {
                args.put("origin_city", request.getOriginCity());
            }
            if (request.getDestinationCity() != null) {
                args.put("destination_city", request.getDestinationCity());
            }
            String text = mcpTool.callTool(toolName, args);
            log.info("规划结果：{}",abbreviate(text));
            RouteInfo routeInfo = AmapRouteParser.parse(text);
            routeInfo.setRouteType(routeType);
            rr.setSuccess(true);
            rr.setMessage("规划完成");
            rr.setData(routeInfo);
            return rr;
        }catch(Exception e){
            log.error("规划路线失败：{}",e.getMessage(),e);
            rr.setMessage("规划路线失败"+e.getMessage());
            rr.setSuccess(false);
        }
        return rr;
    }

    @Override
    public HealthResponse healthCheck() {
        HealthResponse hr=new HealthResponse();
        hr.setStatus("health");
        hr.setService("map-service");
        try {
            List<McpSchema.Tool> tools = mcpTool.getMcpClientWrapper()
                    .listTools()
                    .block(Duration.ofSeconds(10));
            hr.setMapTollsCount(tools == null ? 0 : tools.size());
        } catch (Exception e) {
            log.error("统计MCP工具数量失败: {}", e.getMessage(), e);
            hr.setMapTollsCount(0);
        }
        return hr;
    }

    @Override
    public POIDetailResponse getPOIDetail(String poiId) {
        POIDetailResponse POIdr=new POIDetailResponse();
        if (isBlank(poiId)){
            POIdr.setMessage("poiId不能为空");
            POIdr.setSuccess(false);
            return POIdr;
        }
        try {
            Map<String,Object> args=new HashMap<>();
            args.put("id",poiId);
            String text = mcpTool.callTool(MAPS_SEARCH_DETAIL, args);
            log.info("poi详情：{}",text);
            POIdr.setSuccess(true);
            POIdr.setMessage("获取POI详情成功");
            POIdr.setData(AmapPoiDetailParser.parse(text));

        }catch (Exception e){
            log.error("获取poi详情失败：{}",e.getMessage(),e);
            POIdr.setMessage("获取POI详情失败");
            POIdr.setSuccess(false);
        }
        return POIdr;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String abbreviate(String text) {
        return text.length() <= 200 ? text : text.substring(0, 200) + "...";
    }
}
