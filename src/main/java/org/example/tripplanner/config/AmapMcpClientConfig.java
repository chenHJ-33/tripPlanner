package org.example.tripplanner.config;

import io.agentscope.core.tool.mcp.McpClientBuilder;
import io.agentscope.core.tool.mcp.McpClientWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 高德地图 MCP 服务客户端配置
 *
 * <p>两种传输方式（由 {@code mcp.gaode.transport} 切换）：
 * <ul>
 *   <li>{@code stdio}（默认）：拉起本地子进程 uvx amap-mcp-server，key 通过环境变量传入</li>
 *   <li>{@code http}：直连高德官方托管端点 https://mcp.amap.com/mcp?key=xxx，无需本地进程</li>
 * </ul>
 */
@Configuration
public class AmapMcpClientConfig {

    private static final String AMAP_MCP_HTTP_ENDPOINT = "https://mcp.amap.com/mcp";
    private static final String AMAP_MCP_API_KEY_ENV = "AMAP_MAPS_API_KEY";

    @Bean(destroyMethod = "close")
    @Lazy // 首次注入时才建立连接(stdio 会拉起子进程)，避免启动即连接
    public McpClientWrapper amapMcpClient(
            @Value("${mcp.gaode.key}") String apiKey,
            @Value("${mcp.gaode.transport:stdio}") String transport) {
        McpClientBuilder builder = McpClientBuilder.create("amap");
        if ("http".equalsIgnoreCase(transport)) {
            builder.streamableHttpTransport(AMAP_MCP_HTTP_ENDPOINT)
                    .queryParam("key", apiKey);
        } else {
            builder.stdioTransport("uvx",
                    List.of("amap-mcp-server"),
                    Map.of(AMAP_MCP_API_KEY_ENV, apiKey));
        }
        McpClientWrapper client = builder
                .initializationTimeout(Duration.ofSeconds(60)) // stdio 首次运行 uvx 需要拉包
                .timeout(Duration.ofSeconds(60))
                .buildSync();
        // buildSync 仅构建客户端，必须显式初始化后才可调用工具
        client.initialize().block(Duration.ofSeconds(60));
        return client;
    }
}
