package org.example.tripplanner.tool;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 高德地图 MCP 工具返回结果解析器：把 maps_search_detail 返回的文本(JSON)映射为 Map 并提取图片
 */
public class AmapPoiDetailParser {

    /** 兜底提取 JSON 的正则(结果文本偶尔带说明性前缀) */
    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile("\\{.*\\}", Pattern.DOTALL);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private AmapPoiDetailParser() {
    }

    /**
     * 解析工具返回文本为 Map，并把 photos 数组提取为图片 URL 列表放入 images 键
     *
     * <p>若响应中带 status 字段则校验其值(如直连 REST 格式的场景)。
     *
     * @throws IllegalStateException 高德明确返回失败(status 存在且不为 "1")
     * @throws IllegalArgumentException 文本中不含有效 JSON
     */
    public static Map<String, Object> parse(String text) {
        JsonNode root = readTree(text);
        if (!root.isObject()) {
            throw new IllegalArgumentException("POI详情JSON不是对象节点");
        }
        JsonNode status = root.path("status");
        if (!status.isMissingNode() && !"1".equals(status.asText())) {
            throw new IllegalStateException("高德接口返回失败: status=" + status.asText()
                    + ", info=" + root.path("info").asText(""));
        }
        Map<String, Object> data = OBJECT_MAPPER.convertValue(root, new TypeReference<Map<String, Object>>() {
        });
        data.put("images", extractImageUrls(root.path("photos")));
        return data;
    }

    /**
     * 提取 photos 数组中的图片 URL，兼容 {url:...} 对象与纯字符串两种元素
     */
    static List<String> extractImageUrls(JsonNode photos) {
        List<String> urls = new ArrayList<>();
        if (!photos.isArray()) {
            return urls;
        }
        for (JsonNode photo : photos) {
            String url = photo.isObject() ? photo.path("url").asText(null) : photo.asText(null);
            if (url != null && !url.isBlank()) {
                urls.add(url);
            }
        }
        return urls;
    }

    /**
     * 先按纯 JSON 解析，失败则用正则提取首个 {...} 再试
     */
    private static JsonNode readTree(String text) {
        String candidate = text == null ? "" : text.trim();
        try {
            return OBJECT_MAPPER.readTree(candidate);
        } catch (Exception ignored) {
            // fall through: 尝试从混合文本中提取
        }
        Matcher matcher = JSON_OBJECT_PATTERN.matcher(candidate);
        if (matcher.find()) {
            try {
                return OBJECT_MAPPER.readTree(matcher.group());
            } catch (Exception ignored) {
                // fall through
            }
        }
        throw new IllegalArgumentException("MCP返回内容中未找到有效JSON: "
                + candidate.substring(0, Math.min(candidate.length(), 200)));
    }
}
