package org.example.tripplanner.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.tripplanner.pojo.entity.POIInfo;
import org.example.tripplanner.pojo.request.POISearchRequest;
import org.example.tripplanner.pojo.response.POISearchResponse;
import org.example.tripplanner.service.MapService;
import org.example.tripplanner.tool.AmapPoiParser;
import org.example.tripplanner.tool.MCPTool;
import org.springframework.stereotype.Service;

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

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String abbreviate(String text) {
        return text.length() <= 200 ? text : text.substring(0, 200) + "...";
    }
}
