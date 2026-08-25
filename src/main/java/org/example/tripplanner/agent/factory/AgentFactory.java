package org.example.tripplanner.agent.factory;

import io.agentscope.harness.agent.HarnessAgent;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.example.tripplanner.agent.builder.AttractionAgentBuilder;
import org.example.tripplanner.agent.builder.HotelAgentBuilder;
import org.example.tripplanner.agent.builder.PlannerAgentBuilder;
import org.example.tripplanner.agent.builder.WeatherAgentBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AgentFactory {
    private static final int MAX_AGENT_SETS = 1000;
    @Value("${prompt.version:v1}")
    private String promptVersion;
    @Resource
    private AttractionAgentBuilder attractionAgentBuilder;
    @Resource
    private HotelAgentBuilder hotelAgentBuilder;
    @Resource
    private PlannerAgentBuilder plannerAgentBuilder;
    @Resource
    private WeatherAgentBuilder weatherAgentBuilder;
    @Data
    @Accessors(fluent = true)
    @AllArgsConstructor
    public class AgentSet{
        private HarnessAgent attractionAgent;
        private HarnessAgent hotelAgent;
        private HarnessAgent plannerAgent;
        private HarnessAgent weatherAgent;
    }
    // 会话缓存
    private final Map<String, AgentSet> cache = Collections.synchronizedMap(
            new LinkedHashMap<>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, AgentSet> eldest) {
                    return size() > MAX_AGENT_SETS;
                }
            }
    );
    /** 获取当前会话的一整套 Agent，不存在时按需创建。 */
    public AgentSet get(String sessionId) {
        return cache.computeIfAbsent(cacheKey(sessionId), ignored -> new AgentSet(
                attractionAgentBuilder.build(),
                hotelAgentBuilder.build(),
                weatherAgentBuilder.build(),
                plannerAgentBuilder.build()
        ));
    }

    /** 会话结束时释放该会话的 Agent 集合。 */
    public void remove(String sessionId) {
        cache.remove(cacheKey(sessionId));
    }

    /** 生成包含 Prompt 版本的缓存键。 */
    private String cacheKey(String sessionId) {
        return sessionId + "::" + promptVersion;
    }

}
