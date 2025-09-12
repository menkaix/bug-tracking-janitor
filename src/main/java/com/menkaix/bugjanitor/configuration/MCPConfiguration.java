package com.menkaix.bugjanitor.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.menkaix.bugjanitor.mcptools.ProjectToolsRegistry;

@Configuration
public class MCPConfiguration {

    @Bean
    public ToolCallbackProvider weatherTools(ProjectToolsRegistry projectToolServiceRegistry) {
        return MethodToolCallbackProvider.builder().toolObjects(projectToolServiceRegistry).build();
    }
}