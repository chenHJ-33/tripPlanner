package org.example.tripplanner.agent.builder;

import io.agentscope.core.model.Model;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;
import jakarta.annotation.Resource;
import org.example.tripplanner.agent.loader.PromptLoader;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
@Component
public class HotelAgentBuilder {
    @Resource
    private Model tripModel;
    @Resource
    private PromptLoader promptLoader;
    public HarnessAgent build(){
        return HarnessAgent.builder()
                .name("hotelAgent")
                .model(tripModel)
                .sysPrompt(promptLoader.load("prompts/hotel.txt"))
                .workspace(Paths.get(".agentscope/workspace"))
                .compaction(CompactionConfig.builder()
                        .triggerMessages(30)
                        .keepMessages(10)
                        .build())
                .build();
    }
}
