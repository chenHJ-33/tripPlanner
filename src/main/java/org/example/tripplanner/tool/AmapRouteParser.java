package org.example.tripplanner.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tripplanner.pojo.entity.RouteInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 高德地图 MCP 工具返回结果解析器：把路径规划类工具返回的文本(JSON)映射为 RouteInfo
 */
public class AmapRouteParser {

    /** 兜底提取 JSON 的正则(结果文本偶尔带说明性前缀) */
    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile("\\{.*\\}", Pattern.DOTALL);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private AmapRouteParser() {
    }

    /**
     * 解析工具返回文本，默认取第一条路线方案
     *
     * <p>步行/驾车返回 {@code route.paths[]}，公交返回 {@code route.transits[]}；
     * 若响应中带 status 字段则校验其值(如直连 REST 格式的场景)。
     *
     * @throws IllegalStateException 高德明确返回失败或未返回可用路线
     * @throws IllegalArgumentException 文本中不含有效 JSON
     */
    public static RouteInfo parse(String text) {
        JsonNode root = readTree(text);
        JsonNode status = root.path("status");
        if (!status.isMissingNode() && !"1".equals(status.asText())) {
            throw new IllegalStateException("高德接口返回失败: status=" + status.asText()
                    + ", info=" + root.path("info").asText(""));
        }
        JsonNode route = root.path("route");
        JsonNode paths = route.path("paths");
        if (paths.isArray() && !paths.isEmpty()) {
            return fromPath(paths.get(0));
        }
        JsonNode transits = route.path("transits");
        if (transits.isArray() && !transits.isEmpty()) {
            return fromTransit(transits.get(0));
        }
        throw new IllegalStateException("高德接口未返回可用路线");
    }

    /**
     * 步行/驾车方案：取 path 的总距离与耗时，steps 的 instruction 拼接为路线描述
     */
    static RouteInfo fromPath(JsonNode path) {
        RouteInfo info = new RouteInfo();
        info.setDistance(path.path("distance").asDouble(0D));
        info.setDuration(path.path("duration").asInt(0));
        info.setDescription(joinSteps(path.path("steps")));
        return info;
    }

    /**
     * 公交方案：从 segments 中提取步行指引与乘车线路，拼接为路线描述
     */
    static RouteInfo fromTransit(JsonNode transit) {
        RouteInfo info = new RouteInfo();
        JsonNode distance = transit.path("distance");
        info.setDistance(distance.isValueNode() ? distance.asDouble() : null);
        info.setDuration(transit.path("duration").asInt(0));
        StringBuilder description = new StringBuilder();
        int index = 1;
        for (JsonNode segment : transit.path("segments")) {
            for (JsonNode step : segment.path("walking").path("steps")) {
                String instruction = textOrNull(step, "instruction");
                if (instruction != null) {
                    description.append(index++).append(". ").append(instruction).append('\n');
                }
            }
            for (JsonNode busline : segment.path("bus").path("buslines")) {
                String line = formatBusline(busline);
                if (line != null) {
                    description.append(index++).append(". ").append(line).append('\n');
                }
            }
        }
        info.setDescription(description.toString().trim());
        return info;
    }

    private static String formatBusline(JsonNode busline) {
        String name = textOrNull(busline, "name");
        if (name == null) {
            return null;
        }
        String departure = textOrNull(busline.path("departure_stop"), "name");
        String arrival = textOrNull(busline.path("arrival_stop"), "name");
        if (departure != null && arrival != null) {
            return "乘坐" + name + "(" + departure + " → " + arrival + ")";
        }
        return "乘坐" + name;
    }

    private static String joinSteps(JsonNode steps) {
        StringBuilder description = new StringBuilder();
        int index = 1;
        if (steps.isArray()) {
            for (JsonNode step : steps) {
                String instruction = textOrNull(step, "instruction");
                if (instruction != null) {
                    description.append(index++).append(". ").append(instruction).append('\n');
                }
            }
        }
        return description.toString().trim();
    }

    /**
     * 取字符串字段；缺省、空数组、空串均返回 null(MCP 信封中 road、orientation 缺省时为 [])
     */
    private static String textOrNull(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (!value.isValueNode()) {
            return null;
        }
        String text = value.asText();
        return text.isEmpty() ? null : text;
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
