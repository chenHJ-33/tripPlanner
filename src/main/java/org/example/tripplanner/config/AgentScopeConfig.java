package org.example.tripplanner.config;

import io.agentscope.core.model.Model;
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import io.agentscope.harness.agent.HarnessAgent;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
@Configurable
public class AgentScopeConfig {
    @Value("${agentscope.dashscope.api-key:}")
    private String apiKey;
    @Value("${trip.llm.model:}")
    private String modelName;

    public Model tripModel(){
        return DashScopeChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }
}
