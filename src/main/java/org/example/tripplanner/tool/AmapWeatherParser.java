package org.example.tripplanner.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tripplanner.pojo.entity.WeatherInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 高德地图 MCP 工具返回结果解析器：把 maps_weather 返回的文本(JSON)映射为 WeatherInfo 列表
 */
public class AmapWeatherParser {

    /** 兜底提取 JSON 的正则(结果文本偶尔带说明性前缀) */
    private static final Pattern JSON_OBJECT_PATTERN = Pattern.compile("\\{.*\\}", Pattern.DOTALL);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private AmapWeatherParser() {
    }

    /**
     * 解析工具返回文本
     *
     * <p>amap-mcp-server 返回的是规范化后的信封(直接含 {@code forecasts} 数组，无 status 字段)；
     * 若响应中带 status 字段则校验其值(如直连 REST 格式的场景)，并兼容 REST 的 forecasts[].casts 嵌套结构。
     *
     * @throws IllegalStateException 高德明确返回失败(status 存在且不为 "1")
     * @throws IllegalArgumentException 文本中不含有效 JSON
     */
    public static List<WeatherInfo> parse(String text) {
        JsonNode root = readTree(text);
        JsonNode status = root.path("status");
        if (!status.isMissingNode() && !"1".equals(status.asText())) {
            throw new IllegalStateException("高德接口返回失败: status=" + status.asText()
                    + ", info=" + root.path("info").asText(""));
        }
        List<WeatherInfo> result = new ArrayList<>();
        JsonNode forecasts = root.path("forecasts");
        if (forecasts.isArray()) {
            for (JsonNode forecast : forecasts) {
                // MCP 规范化信封中预报项即 forecasts 元素本身；REST 直连格式则嵌套在 casts 内
                JsonNode casts = forecast.path("casts");
                if (casts.isArray()) {
                    collect(casts, result);
                } else {
                    collectSingle(forecast, result);
                }
            }
        }
        return result;
    }

    private static void collect(JsonNode casts, List<WeatherInfo> result) {
        for (JsonNode cast : casts) {
            collectSingle(cast, result);
        }
    }

    private static void collectSingle(JsonNode item, List<WeatherInfo> result) {
        WeatherInfo info = toWeatherInfo(item);
        if (info != null) {
            result.add(info);
        }
    }

    /**
     * 单个预报节点转 WeatherInfo，缺省字段取类内默认值；非对象节点返回 null
     */
    static WeatherInfo toWeatherInfo(JsonNode cast) {
        if (cast == null || !cast.isObject()) {
            return null;
        }
        WeatherInfo info = new WeatherInfo();
        info.setDate(textOrNull(cast, "date"));
        info.setDayWeather(textOrNull(cast, "dayweather"));
        info.setNightWeather(textOrNull(cast, "nightweather"));
        info.setDayTemp(cast.path("daytemp").asInt(0));
        info.setNightTemp(cast.path("nighttemp").asInt(0));
        info.setWindDirection(textOrNull(cast, "daywind"));
        info.setWindPower(textOrNull(cast, "daypower"));
        return info;
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
