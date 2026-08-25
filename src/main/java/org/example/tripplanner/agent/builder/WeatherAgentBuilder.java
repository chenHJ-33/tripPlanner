package org.example.tripplanner.agent.builder;

import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.model.Model;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.memory.MemoryConfig;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;
import jakarta.annotation.Resource;
import org.example.tripplanner.agent.loader.PromptLoader;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 天气查询agent构建器
 */
@Component
public class WeatherAgentBuilder {
    @Resource
    private Model tripModel;
    @Resource
    private PromptLoader promptLoader;
    public HarnessAgent build(){
        return HarnessAgent.builder()
                .name("weatherAgent")
                .model(tripModel)
                .sysPrompt(promptLoader.load("prompts/weather.txt"))
                .workspace(Paths.get(".agentscope/workspace"))
                .compaction(CompactionConfig.builder()
                        .triggerMessages(30)
                        .keepMessages(10)
                        .build())
                .build();
    }
}
