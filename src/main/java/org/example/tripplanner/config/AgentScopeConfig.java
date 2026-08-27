package org.example.tripplanner.config;

import io.agentscope.core.model.Model;
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentScopeConfig {
    @Value("${agentscope.dashscope.api-key:}")
    private String apiKey;
    @Value("${trip.llm.model:}")
    private String modelName;

    @Bean
    public Model tripModel() {
        return DashScopeChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .build();
    }
}
