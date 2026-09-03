package org.example.tripplanner.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.tripplanner.pojo.response.POIDetailResponse;
import org.example.tripplanner.service.UnsplashService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Unsplash 图片搜索服务：按关键词搜索图片并提取 URL
 */
@Service
@Slf4j
public class UnsplashServiceImpl implements UnsplashService {
    /** 每页返回数量 */
    private static final int PER_PAGE = 5;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Resource
    private  RestClient restClient;
    @Value("${mcp.unsplash.access-key}")
    private  String accessKey;

    @Override
    public POIDetailResponse getPhotoURL(String name) {
        POIDetailResponse response = new POIDetailResponse();
        if (isBlank(name)) {
            response.setSuccess(false);
            response.setMessage("name不能为空");
            return response;
        }
        String photoUrl = searchPhotos(name, PER_PAGE).stream()
                .map(photo -> (String) photo.get("url"))
                .filter(url -> !isBlank(url))
                .findFirst()
                .orElse(null);
        if (photoUrl == null) {
            response.setSuccess(false);
            response.setMessage("未找到相关图片");
            return response;
        }
        response.setSuccess(true);
        response.setMessage("获取图片成功");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", name);
        data.put("photo_url", photoUrl);
        response.setData(data);
        return response;
    }

    @Override
    public List<Map<String, Object>> searchPhotos(String query, int perPage) {
        try {
            String text = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/search/photos")
                            .queryParam("query", query)
                            .queryParam("per_page", perPage)
                            .queryParam("client_id", accessKey)
                            .build())
                    .retrieve()
                    .body(String.class);
            log.info("Unsplash搜索结果: {}", text);
            return extractPhotos(text);
        } catch (Exception e) {
            log.error("Unsplash搜索失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * 解析 results 数组，提取每张图片的 id/url/thumb/description/photographer
     */
    private List<Map<String, Object>> extractPhotos(String text) throws Exception {
        JsonNode root = OBJECT_MAPPER.readTree(text);
        List<Map<String, Object>> photos = new ArrayList<>();
        JsonNode results = root.path("results");
        if (!results.isArray()) {
            return photos;
        }
        for (JsonNode photo : results) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", textOrNull(photo, "id"));
            JsonNode urls = photo.path("urls");
            item.put("url", textOrNull(urls, "regular"));
            item.put("thumb", textOrNull(urls, "thumb"));
            item.put("description", descriptionOrAlt(photo));
            item.put("photographer", textOrNull(photo.path("user"), "name"));
            photos.add(item);
        }
        return photos;
    }

    /** description 为空时回退 alt_description，对应 Python 的 or 逻辑 */
    private static String descriptionOrAlt(JsonNode photo) {
        String description = textOrNull(photo, "description");
        return description != null ? description : textOrNull(photo, "alt_description");
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
