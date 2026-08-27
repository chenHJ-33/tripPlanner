package org.example.tripplanner.tool;

import io.agentscope.core.tool.mcp.McpClientWrapper;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MCP 工具调用封装：绕过 LLM 直接调用高德地图 MCP 工具
 */
@Service
@Slf4j
public class MCPTool {

    /** 单次工具调用超时时间 */
    private static final Duration CALL_TIMEOUT = Duration.ofSeconds(30);

    @Resource
    private McpClientWrapper amapMcpClient;

    /**
     * 获取高德 MCP 客户端(可用于 listTools / 注册到 Toolkit 等)
     */
    public McpClientWrapper getMcpClientWrapper() {
        return amapMcpClient;
    }

    /**
     * 同步调用高德 MCP 工具，返回文本结果(通常为 JSON 字符串)
     *
     * @param toolName 工具名，如 maps_text_search / maps_weather / maps_geo
     * @param args     工具参数
     * @return 所有 TextContent 文本按行拼接的结果
     * @throws IllegalStateException 工具执行失败或超时
     */
    public String callTool(String toolName, Map<String, Object> args) {
        log.info("调用MCP工具: {} 参数: {}", toolName, args);
        McpSchema.CallToolResult result;
        try {
            result = amapMcpClient.callTool(toolName, args).block(CALL_TIMEOUT);
        } catch (Exception e) {
            throw new IllegalStateException("MCP工具调用超时或中断: " + toolName + " - " + e.getMessage(), e);
        }
        if (result == null) {
            throw new IllegalStateException("MCP工具无返回: " + toolName);
        }
        String text = extractText(result.content());
        if (Boolean.TRUE.equals(result.isError())) {
            throw new IllegalStateException("MCP工具执行失败: " + toolName + " - " + text);
        }
        return text;
    }

    /**
     * 提取 CallToolResult 中的全部文本内容
     */
    private String extractText(List<McpSchema.Content> contents) {
        if (contents == null || contents.isEmpty()) {
            return "";
        }
        return contents.stream()
                .filter(c -> c instanceof McpSchema.TextContent)
                .map(c -> ((McpSchema.TextContent) c).text())
                .collect(Collectors.joining("\n"));
    }
}
