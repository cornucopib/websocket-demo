package com.cornucopib.websocketdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket配置类.
 *
 * @author cornucopib
 * @since 2022/1/15
 */
@Configuration
public class WebSocketConfig {

    /**
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
