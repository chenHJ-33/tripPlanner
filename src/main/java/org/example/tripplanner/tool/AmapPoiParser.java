package org.example.tripplanner.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tripplanner.pojo.common.Location;
import org.example.tripplanner.pojo.entity.POIInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 高德地图 MCP 工具返回结果解析器：把 maps_text_search 返回的文本(JSON)映射为 POIInfo 列表
 */
public class AmapPoiParser {

    /** 兜底提取 JSON 的正则(结果文本偶尔带说明性前缀) */
    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
    private static final Pattern LOCATION_PATTERN = Pattern.compile("^\\s*(-?\\d+(?:\\.\\d+)?)\\s*,\\s*(-?\\d+(?:\\.\\d+)?)\\s*$");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private AmapPoiParser() {
    }

    /**
     * 解析工具返回文本
     *
     * <p>amap-mcp-server 返回的是规范化后的信封(直接含 {@code pois} 数组，无 status 字段)；
     * 若响应中带 status 字段则校验其值(如直连 REST 格式的场景)。
     *
     * @throws IllegalStateException 高德明确返回失败(status 存在且不为 "1")
     * @throws IllegalArgumentException 文本中不含有效 JSON
     */
    public static List<POIInfo> parse(String text) {
        JsonNode root = readTree(text);
        JsonNode status = root.path("status");
        if (!status.isMissingNode() && !"1".equals(status.asText())) {
            throw new IllegalStateException("高德接口返回失败: status=" + status.asText()
                    + ", info=" + root.path("info").asText(""));
        }
        JsonNode pois = root.path("pois");
        List<POIInfo> result = new ArrayList<>();
        if (pois.isArray()) {
            for (JsonNode poi : pois) {
                POIInfo info = toPoiInfo(poi);
                if (info != null) {
                    result.add(info);
                }
            }
        }
        return result;
    }

    /**
     * 单个 poi 节点转 POIInfo，缺省字段置 null；非对象节点返回 null
     */
    static POIInfo toPoiInfo(JsonNode poi) {
        if (poi == null || !poi.isObject()) {
            return null;
        }
        POIInfo info = new POIInfo();
        info.setId(textOrNull(poi, "id"));
        info.setName(textOrNull(poi, "name"));
        info.setType(textOrNull(poi, "type"));
        info.setAddress(textOrNull(poi, "address"));
        info.setLocation(parseLocation(poi.path("location").asText(null)));
        info.setTel(textOrNull(poi, "tel"));
        return info;
    }

    /**
     * 解析 "经度,纬度" 字符串为 Location，格式不合法时返回 null
     */
    static Location parseLocation(String location) {
        if (location == null) {
            return null;
        }
        Matcher matcher = LOCATION_PATTERN.matcher(location);
        if (!matcher.matches()) {
            return null;
        }
        Location loc = new Location();
        loc.setLongitude(Double.parseDouble(matcher.group(1)));
        loc.setLatitude(Double.parseDouble(matcher.group(2)));
        return loc;
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
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
