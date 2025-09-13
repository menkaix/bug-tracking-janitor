package com.menkaix.bugjanitor.configuration;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.menkaix.bugjanitor.mcptools.ProjectToolsRegistry;

@Configuration
public class MCPConfiguration {

    @Bean
    public ToolCallbackProvider projectTools(ProjectToolsRegistry projectToolsRegistry) {
        return MethodToolCallbackProvider.builder().toolObjects(projectToolsRegistry).build();
    }
}
